package com.atguigu.gmall.cart.client;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@FeignClient("service-cart")
public interface CartFeignClient {


    @RequestMapping("/api/cart/addToCart/{skuId}/{skuNum}")
    public void addToCart(@PathVariable("skuId") Long skuId, @PathVariable("skuNum")Integer skuNum);

    @RequestMapping("/api/cart/cartList")
    Result<List> cartList();



    @RequestMapping("/api/cart/getIsCheckedCartList/{userId}")
    List<OrderDetail> getIsCheckedCartList(@PathVariable("userId")String userId);

    @GetMapping("/api/cart/getCartCheckedList/{userId}")
    List<CartInfo> getCartCheckedList(@PathVariable("userId") String userId);
}
