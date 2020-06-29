package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service

public interface ManageService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(String category1Id);

    List<BaseCategory3> getCategory3(String category2Id);

    IPage<SpuInfo> selectPage(Page<SpuInfo> spuInfoPage, QueryWrapper<SpuInfo> spuInfoQueryWrapper);
     List<SpuSaleAttr> getSpuSaleAttrList(Long spuId);

    List<SpuImage> getSpuImageList(Long spuId);


    IPage<SkuInfo> selectPage(Page<SkuInfo> pageParam);
    /**
     * 商品上架
     * @param skuId
     */
    void onSale(Long skuId);
    void saveskuInfo(SkuInfo skuInfo);


    /**
     * 商品下架
     * @param skuId
     */
    void cancelSale(Long skuId);

    SkuInfo getSkuInfo(Long skuId);
    BigDecimal getSkuPrice(Long skuId);


    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId);

    Map getSkuValueIdsMap(Long spuId);
}
