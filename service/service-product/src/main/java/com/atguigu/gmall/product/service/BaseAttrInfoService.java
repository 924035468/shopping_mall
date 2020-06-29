package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.model.product.BaseCategoryView;

import java.util.List;

public interface BaseAttrInfoService  {
    List<BaseAttrInfo> getArrtInfo(String category1Id, String category2Id, String category3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrVlue(Long attrId);

    BaseCategoryView getCategoryView();
    List<SearchAttr> getSearchAttrs(Long skuId);

}
