package com.atguigu.gmall.cart.Service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CartService {
    void addToCart(Long skuId, Integer skuNum, HttpServletRequest request
                   );

    List<CartInfo> cartList(String userIdParam);

    void checkCart(Long skuId, Integer check, HttpServletRequest request);

    List<OrderDetail> getIsCheckedCartList(String userId);
}
