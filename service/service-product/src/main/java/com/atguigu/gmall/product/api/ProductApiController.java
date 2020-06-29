package com.atguigu.gmall.product.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseCategoryViewService;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.ManageService;
import com.atguigu.gmall.product.service.impl.BaseAttrInfoServiceImpl;
import com.atguigu.gmall.product.service.impl.ManageServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Api(description="ProductApiController")
@RequestMapping("api/product")
@RestController

public class ProductApiController {
    @Autowired
    private ManageService manageService;

    @Autowired
    ManageServiceImpl manageServiceImpl;
    @Autowired
    private BaseCategoryViewService baseCategoryViewService;

    @Autowired
    private BaseAttrInfoServiceImpl baseAttrInfoService;


    @Autowired
    private BaseTrademarkService baseTrademarkService;

    @Autowired
    BaseCategoryViewService BaseCategoryViewService;

    @RequestMapping("getBaseCategoryList")
    Result getBaseCategoryList(){

        List<JSONObject> list = BaseCategoryViewService.getBaseCategoryList();

        return Result.ok(list);
    }




/*    @Autowired
    private BaseAttrInfoService baseAttrInfoService;*/




    //查询info





    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        return skuInfo;
    }



    @RequestMapping("inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id) {
        BaseCategoryView baseCategoryView = baseCategoryViewService.getCategoryViewByCategory3Id(category3Id);
        return baseCategoryView;
    }

    @RequestMapping("inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("skuId") Long skuId, @PathVariable("spuId") Long spuId) {
        List<SpuSaleAttr> spuSaleAttrs = manageServiceImpl.getSpuSaleAttrListCheckBySku(skuId,spuId);
        return spuSaleAttrs;
    }

    @RequestMapping("inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId) {
        return manageServiceImpl.getSkuPrice(skuId);
    }


    @RequestMapping("inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId){
        Map map = manageServiceImpl.getSkuValueIdsMap(spuId);

        return map;
    }




    @RequestMapping("inner/getTrademarkByTmId/{tmId}")
    public BaseTrademark getTrademarkByTmId(@PathVariable("tmId") Long tmId){

        BaseTrademark baseTrademark = baseTrademarkService.getById(tmId);


        return  baseTrademark;

    }


    @RequestMapping("inner/getAttrList/{skuId}")
    List<SearchAttr> getAttrList(@PathVariable("skuId") Long skuId){


        List<SearchAttr> SearchAttrs = baseAttrInfoService.getSearchAttrs(skuId);
        return SearchAttrs;
    };



}
