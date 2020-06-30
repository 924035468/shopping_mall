package com.atguigu.gmall.cart.Service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

public interface CartInfoService {
    List<CartInfo> getCartCheckedList(String userId);
}
