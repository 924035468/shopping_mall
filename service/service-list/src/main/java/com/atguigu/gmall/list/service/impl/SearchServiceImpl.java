package com.atguigu.gmall.list.service.impl;


import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.list.repository.GoodsRepository;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.*;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.productclient.ProductFeignClient;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


        @Autowired
        private RedisTemplate redisTemplate;


    @Override
    public void upperGoods(Long skuId) {

        Goods goods = new Goods();

        //查询skuInfo

        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);


        //查询category

        if(skuInfo!=null) {

            // 封装商品数据
            goods.setDefaultImg(skuInfo.getSkuDefaultImg());
            goods.setPrice(skuInfo.getPrice().doubleValue());
            goods.setId(skuInfo.getId());
            goods.setTitle(skuInfo.getSkuName());
            goods.setCreateTime(new Date());
            // 查询分类数据
            BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            if (baseCategoryView != null) {
                goods.setCategory1Id(baseCategoryView.getCategory1Id());
                goods.setCategory1Name(baseCategoryView.getCategory1Name());
                goods.setCategory2Id(baseCategoryView.getCategory2Id());
                goods.setCategory2Name(baseCategoryView.getCategory2Name());
                goods.setCategory3Id(baseCategoryView.getCategory3Id());
                goods.setCategory3Name(baseCategoryView.getCategory3Name());
            }

            //查询trademark
            BaseTrademark baseTrademark = productFeignClient.getTrademarkByTmId(skuInfo.getTmId());
            if (baseTrademark != null){
                goods.setTmId(skuInfo.getTmId());
                goods.setTmName(baseTrademark.getTmName());
                goods.setTmLogoUrl(baseTrademark.getLogoUrl());

            }

            //查询
            List<SearchAttr> searchAttrs = productFeignClient.getAttrList(skuId);

            // 封装平台属性
            goods.setAttrs(searchAttrs);

            System.out.println(goods);
            goodsRepository.save(goods);
        }
    }

    @Override
    public void downGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }



    @Override
    public SearchResponseVo list(SearchParam searchParam) {


        SearchRequest searchRequest = this.buildQueryDsl(searchParam);

        SearchResponse searchResponse = null;


        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        } catch (IOException e) {
            e.printStackTrace();
        }

        SearchResponseVo searchResponseVo = this.parseSearchResult(searchResponse);


        return searchResponseVo;
    }

    @Override
    public void incrHotScore(Long skuId) {
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        Double hotScore = zSetOperations.incrementScore("hotScore", "sku:", 1);
        if(hotScore%10 == 0){

/*            Optional<Goods> byid = goodsRepository.findById(skuId);
            Goods goods = byid.get();

            goods.setHotScore(Math.round(hotScore));
            goodsRepository.save(goods);*/


        }

    }

    //构建dsl搜索语句
    public static SearchRequest buildQueryDsl(SearchParam searchParam){

/*
        // 分页参数
        Integer pageSize = searchParam.getPageSize();
        Integer pageNo = searchParam.getPageNo();

        // 品牌
        String trademark = searchParam.getTrademark();

        // sku中所包含的平台属性集合
        String[] props = searchParam.getProps();

        // 排序
        String order = searchParam.getOrder();

        // 关键字
        String keyword = searchParam.getKeyword();

        // 分类id
        Long category1Id = searchParam.getCategory1Id();
        Long category2Id = searchParam.getCategory2Id();
        Long category3Id = searchParam.getCategory3Id();



        //创建一条空的elasticsearch sql语句 无条件查询
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if(null != keyword){
            boolQueryBuilder.must(new MatchQueryBuilder("title", keyword));
            //定义高亮样式

            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title");
            highlightBuilder.postTags("</span>");
            highlightBuilder.preTags("<span style='color:red;font-weight:bolder;'>");
            searchSourceBuilder.highlighter(highlightBuilder);



        }

        if(null != category1Id){
            boolQueryBuilder.filter(new TermQueryBuilder("category1Id",category1Id));
        }
        if(null != category2Id){
            boolQueryBuilder.filter(new TermQueryBuilder("category2Id",category2Id));
        }
        if(null != category3Id){
            boolQueryBuilder.filter(new TermQueryBuilder("category3Id",category3Id));
        }


        // 属性条件
        //props=23:4G:运行内存
        //平台属性Id 平台属性值名称 平台属性名，

        if(null != props && props.length>0 ){
            for (String prop:props) {
                // prop = 23:4G:运行内存
                String[] split = prop.split(":");
                String attrId = split[0];//属性id
                String attrValue = split[1];//属性值名称
                String attrName = split[2];
                BoolQueryBuilder subBoolQueryForProps = new BoolQueryBuilder();
                subBoolQueryForProps.must(new MatchQueryBuilder("attrValue", attrValue));
                subBoolQueryForProps.must(new MatchQueryBuilder("attrId", attrId));
                BoolQueryBuilder boolQueryForProps = new BoolQueryBuilder();
                boolQueryForProps.must(new NestedQueryBuilder("attrs", subBoolQueryForProps, ScoreMode.None));
                boolQueryBuilder.filter(boolQueryForProps);
            }
        }
*//*        //商标聚合
        TermsAggregationBuilder termsAggregationBuilderTradeMark = AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNmeAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));*//*


        // 商标聚合
        TermsAggregationBuilder termsAggregationBuilderTradeMark = AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(termsAggregationBuilderTradeMark);
        System.out.println(searchSourceBuilder);


*//*        //设置平台属性聚合
        NestedAggregationBuilder nestedAggregationBuilderProps = AggregationBuilders.nested("attrAgg", "attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName")
                                .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))
                        ));*//*

        //  设置平台属性聚合
        NestedAggregationBuilder nestedAggregationBuilderProps = AggregationBuilders.nested("attrAgg", "attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue")));

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(termsAggregationBuilderTradeMark);
        searchSourceBuilder.aggregation(nestedAggregationBuilderProps);


        //构建分页
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.from((pageNo-1)*pageNo);


        // 设置搜索的库和表封装搜索请求对象
        SearchRequest searchRequest = new SearchRequest("goods");
        searchRequest.types("info");
        searchRequest.source(searchSourceBuilder);

       *//* // 打印dsl语句*//*
        System.out.println(searchSourceBuilder.toString());*/

        // 分页参数
        Integer pageSize = searchParam.getPageSize();
        Integer pageNo = searchParam.getPageNo();

        // 品牌
        String trademark = searchParam.getTrademark();

        // sku中所包含的平台属性集合
        String[] props = searchParam.getProps();

        // 排序
        String order = searchParam.getOrder();

        // 关键字
        String keyword = searchParam.getKeyword();

        // 分类id
        Long category1Id = searchParam.getCategory1Id();
        Long category2Id = searchParam.getCategory2Id();
        Long category3Id = searchParam.getCategory3Id();







        // 总{}语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // bool复合查询
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if(StringUtils.isNotBlank(order)){

            String[] split = order.split(":");
            String type = split[0];
            String sort = split[1];

            if(type.equals("1")){
                type = "hotScore";

            }else {
                type = "price";
            }

             searchSourceBuilder.sort(type, sort.equals("asc") ? SortOrder.ASC : SortOrder.DESC);


        }
        // 根据检索条件封装复合查询
        if (StringUtils.isNotBlank(keyword)) {
            boolQueryBuilder.must(new MatchQueryBuilder("title", keyword));

            // 可以使用默认高亮样式，也可以自定义高亮样式
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title");
            highlightBuilder.postTags("</span>");
            highlightBuilder.preTags("<span style='color:red;font-weight:bolder;'>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        if (null != category1Id) {
            boolQueryBuilder.filter(new TermQueryBuilder("category1Id", category1Id));
        }
        if (null != category2Id) {
            boolQueryBuilder.filter(new TermQueryBuilder("category2Id", category2Id));
        }
        if (null != category3Id) {
            boolQueryBuilder.filter(new TermQueryBuilder("category3Id", category3Id));
        }

        // 分页
        searchSourceBuilder.from(0);// （当前页面-1)*60
        searchSourceBuilder.size(60);

        // 属性条件
        if (null != props && props.length > 0) {
            for (String prop : props) {
                // prop = 23:4G:运行内存
                String[] split = prop.split(":");
                String attrId = split[0];//属性id
                String attrValue = split[1];//属性值名称
                String attrName = split[2];

                // 最内层bool查询，匹配查询
                BoolQueryBuilder subBoolQueryForProps = new BoolQueryBuilder();
                subBoolQueryForProps.must(new MatchQueryBuilder("attrs.attrValue", attrValue));
                subBoolQueryForProps.must(new MatchQueryBuilder("attrs.attrId", attrId));

                // 第二层嵌套匹配条件bool查询
                BoolQueryBuilder boolQueryForProps = new BoolQueryBuilder();
                boolQueryForProps.must(new NestedQueryBuilder("attrs", subBoolQueryForProps, ScoreMode.None));

                // 封装进外层的bool，过滤
                boolQueryBuilder.filter(boolQueryForProps);
            }
        }


        //sort
        // 商标聚合
        TermsAggregationBuilder termsAggregationBuilderTradeMark = AggregationBuilders.terms("tmIdAgg").field("tmId")
                                                    .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                                                    .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));


        //  设置平台属性聚合
        NestedAggregationBuilder nestedAggregationBuilderProps = AggregationBuilders.nested("attrAgg", "attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue")));


        // 将复合搜素的条件放入query
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(termsAggregationBuilderTradeMark);
        searchSourceBuilder.aggregation(nestedAggregationBuilderProps);

        // 设置搜索的库和表封装搜索请求对象
        SearchRequest searchRequest = new SearchRequest("goods");
        searchRequest.types("info");
        searchRequest.source(searchSourceBuilder);
        // 打印dsl语句
        System.out.println(searchSourceBuilder.toString());



        return searchRequest;
    }

        //解析返回结果
    public static SearchResponseVo parseSearchResult(SearchResponse searchResponse){


        SearchResponseVo searchResponseVo = new SearchResponseVo();

        SearchHits hits = searchResponse.getHits();

        SearchHit[] sourceHits = hits.getHits();

        List<Goods> goods = new ArrayList<>();
        for (SearchHit sourceHit:sourceHits
             ) {
            String sourceAsString = sourceHit.getSourceAsString();
            Goods good = JSON.parseObject(sourceAsString, Goods.class);
            //解析高亮
            Map<String, HighlightField> highlightFields = sourceHit.getHighlightFields();

            HighlightField title = highlightFields.get("title");
            if(null!=title){
                Text[] fragments = title.getFragments();
                Text fragment = fragments[0];
                good.setTitle(fragment.toString());

            }
            goods.add(good);


        }

        //解析聚合:品牌、属性
        searchResponseVo.setGoodsList(goods);
        List<SearchResponseTmVo> searchResponseTmVos = new ArrayList<>();
        Aggregations aggregations = searchResponse.getAggregations();


        ParsedLongTerms tmIdAgg = (ParsedLongTerms) aggregations.getAsMap().get("tmIdAgg");

        List<? extends Terms.Bucket> buckets = tmIdAgg.getBuckets();

        searchResponseTmVos = buckets.stream().map(bucket->{
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
           // 解析过程
            // 解析商标id
            Long tmId =  bucket.getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);
            // 解析商标名称

            ParsedStringTerms tmNameAgg = (ParsedStringTerms) bucket.getAggregations().getAsMap().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            searchResponseTmVo.setTmName(tmName);


            ParsedStringTerms tmLogoUrlAgg = (ParsedStringTerms) bucket.getAggregations().getAsMap().get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            searchResponseTmVo.setTmLogoUrl(tmLogoUrl);

            return searchResponseTmVo;

        }).collect(Collectors.toList());



        searchResponseVo.setTrademarkList(searchResponseTmVos);
        ParsedNested parsedNested = (ParsedNested) aggregations.getAsMap().get("attrAgg");


        ParsedLongTerms attrIdAgg = (ParsedLongTerms) parsedNested.getAggregations().get("attrIdAgg");

        List<? extends Terms.Bucket> attrIdAggBuckets = attrIdAgg.getBuckets();

      List<SearchResponseAttrVo> searchResponseAttrVos = attrIdAggBuckets.stream().map(bucket -> {
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();


            long attrId = bucket.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);


            ParsedStringTerms parsedStringTerms = (ParsedStringTerms) bucket.getAggregations().asMap().get("attrNameAgg");
            String attrName = parsedStringTerms.getBuckets().get(0).getKeyAsString();
            searchResponseAttrVo.setAttrName(attrName);

            // 封装属性值集合的聚合结果
            List<String> attrValues = new ArrayList<>();
            ParsedStringTerms parsedStringTermsForValue = (ParsedStringTerms) bucket.getAggregations().asMap().get("attrValueAgg");
            List<? extends Terms.Bucket> bucketsForValue = parsedStringTermsForValue.getBuckets();
            for (Terms.Bucket bucket1 : bucketsForValue) {
                String keyAsString = bucket1.getKeyAsString();
                attrValues.add(keyAsString);
            }
            searchResponseAttrVo.setAttrValueList(attrValues);

            return searchResponseAttrVo;


        }).collect(Collectors.toList());

        searchResponseVo.setAttrsList(searchResponseAttrVos);//nested

        return searchResponseVo;

/*        SearchHits hits1 = searchResponse.getHits();
        SearchHit[] hits2 = hits1.getHits();
        SearchHit[] hits = searchResponse.getHits().getHits();
        ArrayList<Goods> goods1 = new ArrayList<>();

        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            Goods  goods = JSON.parseObject(sourceAsString, Goods.class);
            goods1.add(goods);
        }
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        searchResponseVo.setGoodsList(goods1);
        return searchResponseVo;*/



    }


/*    public static void main(String[] args) {
        SearchServiceImpl searchService = new SearchServiceImpl();

        searchService.list();


    }*/
}
