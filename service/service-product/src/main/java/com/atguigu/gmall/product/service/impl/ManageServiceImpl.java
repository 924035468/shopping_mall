package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;

import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;


import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ManageServiceImpl implements ManageService {
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private  RedisTemplate redisTemplate;

    @Override
    public Map getSkuValueIdsMap(Long spuId) {

        List<Map> list = skuSaleAttrValueMapper.selectSaleAttrValuesBySpu(spuId);

        // 返回结果的map
        Map<String ,String> map = new HashMap<>();

        // 处理将返回结果封装给map
        // 循环遍历
        if(list!=null&&list.size()>0){
            for (Map<String,String> skuMap : list) {// 数据map
                // key = 125|123 ,value = 37
                map.put(skuMap.get("value_ids"), skuMap.get("sku_id"));
            }
        }

        return map;
    }
    @Override
    public  List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {

        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuId,spuId);

        return spuSaleAttrs;

    }

    @Override
    public IPage<SkuInfo> selectPage(Page<SkuInfo> pageParam) {
        QueryWrapper<SkuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<SkuInfo> page = skuInfoMapper.selectPage(pageParam, queryWrapper);
        return page;
    }

    @Override

    public void onSale(Long skuId) {
// 更改销售状态
        SkuInfo skuInfoUp = new SkuInfo();
        skuInfoUp.setId(skuId);
        skuInfoUp.setIsSale(1);
        skuInfoMapper.updateById(skuInfoUp);
    }

    @Override

    public void cancelSale(Long skuId) {
// 更改销售状态
        SkuInfo skuInfoUp = new SkuInfo();
        skuInfoUp.setId(skuId);
        skuInfoUp.setIsSale(0);
        skuInfoMapper.updateById(skuInfoUp);
    }

  //AOC缓存锁先注释掉
    //@GmallCache
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        SkuInfo skuInfo = getSkuInfoDB(skuId);
        return skuInfo;

    }








    /*public SkuInfo getSkuInfobak(Long skuId) {

        SkuInfo skuInfo = null;
        String skuRedisKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        //查询缓存 ,缓存中的商品详情key
        String skuInfoStr = (String)redisTemplate.opsForValue().get(skuRedisKey);
        if(StringUtils.isNotBlank(skuInfoStr)){
            skuInfo = JSON.parseObject(skuInfoStr,SkuInfo.class);
        }
        if(skuInfo == null){
            String uuid = UUID.randomUUID().toString();
            Boolean OK = redisTemplate.opsForValue().setIfAbsent("sku:" + skuId + ":lock", uuid, 10, TimeUnit.SECONDS);

            if(OK){
                skuInfo = getSkuInfoDB(skuId);
                if(skuInfo == null){
                    SkuInfo skuInfo1 = new SkuInfo();
                    redisTemplate.opsForValue().set(skuRedisKey,JSON.toJSONString(skuInfo1),60*60,TimeUnit.SECONDS);
                    return skuInfo1;
                }
                redisTemplate.opsForValue().set(skuRedisKey, JSON.toJSONString(skuInfo));
                // 使用lua脚本删除分布式锁 // lua，在get到key后，根据key的具体值删除key
                DefaultRedisScript<Long> luaScript = new DefaultRedisScript<>();
                //luaScript.setResultType(Long.class);
                luaScript.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
                redisTemplate.execute(luaScript, Arrays.asList("sku:" + skuId + ":lock"), uuid);
                return skuInfo;

            }else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuInfo(skuId);
            }


        }


        return skuInfo;
  }*/
      /* String skuRedisKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        SkuInfo skuInfo = null;

        String skuInfoStr = (String)redisTemplate.opsForValue().get(skuRedisKey);
        if(StringUtils.isBlank(skuInfoStr)){
            SkuInfo skuInfoDB = getSkuInfoDB(skuId);

            String s = JSON.toJSONString(skuInfoDB);
            redisTemplate.opsForValue().set(skuRedisKey,s);

        }else {
            skuInfo = JSON.parseObject(skuInfoStr,SkuInfo.class);

        }
        return skuInfo;*/



    private SkuInfo getSkuInfoDB(Long skuId) {

/*        QueryWrapper<SkuInfo> skuInfoQueryWrapper = new QueryWrapper<>();
        skuInfoQueryWrapper.eq("id", skuId);
        SkuInfo skuInfo = skuInfoMapper.selectOne(skuInfoQueryWrapper);
        QueryWrapper<SkuImage> skuImageQueryWrapper = new QueryWrapper<>();
        skuImageQueryWrapper.eq("sku_id",skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(skuImageQueryWrapper);
        skuInfo.setSkuImageList(skuImages);
        //查询skuAttrValueList
        QueryWrapper<SkuAttrValue> skuAttrValueQueryWrapper = new QueryWrapper<>();
        skuAttrValueQueryWrapper.eq("sku_id",skuId);
        List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.selectList(skuAttrValueQueryWrapper);

        skuInfo.setSkuAttrValueList(skuAttrValues);
        //查询skuSaleAttrValueList
        QueryWrapper<SkuSaleAttrValue> skuSaleAttrValueQueryWrapper = new QueryWrapper<>();
        skuSaleAttrValueQueryWrapper.eq("sku_id",skuId);
        List<SkuSaleAttrValue> skuSaleAttrValues = skuSaleAttrValueMapper.selectList(skuSaleAttrValueQueryWrapper);
        skuInfo.setSkuSaleAttrValueList(skuSaleAttrValues);
        return skuInfo;*/


        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        QueryWrapper<SkuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id",skuId);
        List<SkuImage> skuImages = skuImageMapper.selectList(queryWrapper);

        skuInfo.setSkuImageList(skuImages);
        return skuInfo;


    }
    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        QueryWrapper<SpuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id", spuId);
        return spuImageMapper.selectList(queryWrapper);
    }
    @Override
    public List<BaseCategory1> getCategory1() {


        List<BaseCategory1> baseCategory1s = baseCategory1Mapper.selectList(null);
        return baseCategory1s;
    }

    @Override
    public List<BaseCategory2> getCategory2(String category1Id) {
        QueryWrapper wrapper = new QueryWrapper<BaseCategory2>();
        wrapper.eq("category1_id",category1Id);
        List<BaseCategory2> baseCategory2s = baseCategory2Mapper.selectList(wrapper);
        return baseCategory2s;
    }

    @Override
    public List<BaseCategory3> getCategory3(String category2Id) {
        QueryWrapper wrapper = new QueryWrapper<BaseCategory3>();
        wrapper.eq("category2_id",category2Id);
        List<BaseCategory3> baseCategory3s = baseCategory3Mapper.selectList(wrapper);
        return baseCategory3s;


    }

    @Override
    public IPage<SpuInfo> selectPage(Page<SpuInfo> spuInfoPage, QueryWrapper<SpuInfo> spuInfoQueryWrapper) {
        IPage<SpuInfo> spuInfoIPage = spuInfoMapper.selectPage(spuInfoPage, spuInfoQueryWrapper);

        return spuInfoIPage;
    }


    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }


    @Override
    public void saveskuInfo(SkuInfo skuInfo) {

        skuInfoMapper.insert(skuInfo);
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList != null && skuImageList.size() >0) {


            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            }
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();

        if (!CollectionUtils.isEmpty(skuSaleAttrValueList)) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
    }
    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        BigDecimal price = skuInfo.getPrice();
        return price;



    }

}
