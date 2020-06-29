package com.atguigu.gmall.itemclient;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value ="service-item")
public interface ItemFeignClient {

    @RequestMapping("api/item/{skuId}")
    Result getItem(@PathVariable("skuId") Long skuId);
}
