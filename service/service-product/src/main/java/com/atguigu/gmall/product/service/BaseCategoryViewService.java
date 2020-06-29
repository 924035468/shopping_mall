package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.BaseCategoryView;

import java.util.List;

public interface BaseCategoryViewService {
    BaseCategoryView getCategoryViewByCategory3Id(Long category3Id);

    List<JSONObject> getBaseCategoryList();
}
