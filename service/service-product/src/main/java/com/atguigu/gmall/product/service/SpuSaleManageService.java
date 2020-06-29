package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;

import java.util.List;

public interface SpuSaleManageService {


    void saveSpuSaleInfo(SpuInfo spuInfo);

    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId);

}
