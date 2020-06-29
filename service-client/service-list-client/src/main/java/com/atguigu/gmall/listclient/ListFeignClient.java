package com.atguigu.gmall.listclient;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.SearchParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value="service-list")
public interface ListFeignClient {


    @GetMapping("/api/list/inner/upperGoods/{skuId}")
    Result upperGoods(@PathVariable("skuId") Long skuId);
    @GetMapping("/api/list/inner/downGoods/{skuId}")
    Result downGoods(@PathVariable("skuId") Long skuId);


    @PostMapping("/api/list/list")
    Result<Map> list(@RequestBody SearchParam searchParam);

    @RequestMapping("api/list/inner/incrHotScore/{skuId}")
    void incrHotScore(@PathVariable("skuId") Long skuId);

}
