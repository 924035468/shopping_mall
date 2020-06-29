package com.atguigu.gmall.list.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.client.ElasticsearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Api(description = "ddd")
@RestController
@RequestMapping("api/list")

public class ListController {


    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private SearchService searchService;


    @RequestMapping("inner/incrHotScore/{skuId}")
    void incrHotScore(@PathVariable("skuId") Long skuId){
        searchService.incrHotScore(skuId);
    }

    @ApiOperation("bb")
    @PostMapping("list")
    Result<Map> list(@RequestBody SearchParam searchParam) {

        SearchResponseVo searchResponseVo = searchService.list(searchParam);

        Map<String, Object> map = new HashMap<>();
        map.put("trademarkList", searchResponseVo.getTrademarkList());
        map.put("attrsList", searchResponseVo.getAttrsList());
        map.put("goodsList", searchResponseVo.getGoodsList());
        return Result.ok(map);

    }

    ;

    @GetMapping("inner/downGoods/{skuId}")
    public Result downGoods(@PathVariable("skuId") Long skuId) {
        searchService.downGoods(skuId);
        return Result.ok();
    }


    @RequestMapping("inner/createIndex")
    public void setList() {

        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);

    }

    @GetMapping("inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId) {

        searchService.upperGoods(skuId);


        return Result.ok();

    }

}
