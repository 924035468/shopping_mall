package com.atguigu.gmall.list.service;

import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;

public interface SearchService {
    void upperGoods(Long skuId);

    void downGoods(Long skuId);

    SearchResponseVo list(SearchParam searchParam);

    void incrHotScore(Long skuId);
}
