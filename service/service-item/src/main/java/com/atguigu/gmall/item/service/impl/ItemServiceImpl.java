package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.listclient.ListFeignClient;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.productclient.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;


@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired

    ListFeignClient listFeignClient;

    @Override
    public Map<String, Object> getSkuById(Long skuId) {
        Map<String, Object> map = new HashMap<>();

        CompletableFuture completableFuture = new CompletableFuture();




        CompletableFuture<SkuInfo> skuInfoCompletableFuture = completableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                // sku信息
                SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

                map.put("skuInfo",skuInfo);

                return skuInfo;
            }
        },threadPoolExecutor);



        CompletableFuture<Void> price = completableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
                map.put("price", skuPrice);

            }
        },threadPoolExecutor);




        CompletableFuture categoryView = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
                map.put("categoryView", categoryView);

            }
        },threadPoolExecutor);

        // 商品被访问，为搜索增加热度值
        listFeignClient.incrHotScore(skuId);

        CompletableFuture spuSaleAttrList = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                // 页面销售属性列表信息
                List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
                map.put("spuSaleAttrList", spuSaleAttrListCheckBySku);
            }
        },threadPoolExecutor);
        ;



        CompletableFuture valuesSkuJson = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {

                // 页面销售属性列表信息
                // 页面销售属性map
                Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
                // 放入销售属性对应skuId的map
                map.put("valuesSkuJson", JSON.toJSONString(skuValueIdsMap));
            }
        },threadPoolExecutor);




        completableFuture.allOf(skuInfoCompletableFuture,price,categoryView,spuSaleAttrList,valuesSkuJson).join();
        return map;
    }



    public Map<String, Object> getSkuByIdbak(Long skuId) {
        Map<String, Object> map = new HashMap<>();



        // sku信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        // 分类信息
        BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());

        // 价格信息
        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);

        // 页面销售属性列表信息
        List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());

        // 页面销售属性map
        Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());

        map.put("price",skuPrice);
        map.put("categoryView",categoryView);
        map.put("skuInfo",skuInfo);
        map.put("spuSaleAttrList",spuSaleAttrListCheckBySku);
        // 放入销售属性对应skuId的map
        map.put("valuesSkuJson", JSON.toJSONString(skuValueIdsMap));
        return map;
    }
}
