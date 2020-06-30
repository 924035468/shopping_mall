package com.atguigu.gmall.order.client;

import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("service-order")
public interface OrderFeignClient {

    @PostMapping("api/order/inner/getTradeNo/{userId}")
    String getTradeNo(@PathVariable("userId") String userId);

    @PostMapping("api/order/inner/getOrderInfo/{orderId}")
    OrderInfo getOrderInfo(@PathVariable("orderId") String orderId);
}
