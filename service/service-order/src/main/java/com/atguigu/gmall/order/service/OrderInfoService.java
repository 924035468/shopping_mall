package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

public interface OrderInfoService {
    String getTradeNo(String userId);

    OrderInfo getOrderInfo(String orderId);

    boolean checkTradeNo(String userId, String tradeNo);

    OrderInfo saveOrderInfo(OrderInfo orderInfo);
}
