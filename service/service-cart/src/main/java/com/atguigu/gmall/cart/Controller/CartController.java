package com.atguigu.gmall.cart.Controller;


import com.atguigu.gmall.cart.Service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {




    @Autowired
    private CartService cartService;

    @RequestMapping("/getIsCheckedCartList/{userId}")
    List<OrderDetail> getIsCheckedCartList(@PathVariable("userId")String userId){
        List<OrderDetail> isCheckCartInfos= cartService.getIsCheckedCartList(userId);
        return isCheckCartInfos;

    };
    //http://api.gmall.com/api/cart/checkCart/11/1

    @RequestMapping("checkCart/{skuId}/{check}")
    public  Result checkCart( @PathVariable("skuId") Long skuId,@PathVariable("check") Integer check,HttpServletRequest request){

        cartService.checkCart(skuId, check, request);
        String userId = request.getHeader("userId");
        List<CartInfo> cartList= cartService.cartList(userId);


        return Result.ok(cartList);
    }


    @RequestMapping("cartList")
    Result<List> cartList(HttpServletRequest request){

        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
        String userIdParam = "";

        if(StringUtils.isNotBlank(userId)){

            userIdParam = userId;
        }else {
            userIdParam = userTempId;
        }
        List<CartInfo> cartList= cartService.cartList(userIdParam);


        return Result.ok(cartList);

    };

    @RequestMapping("addToCart/{skuId}/{skuNum}")
    public void addToCart( @PathVariable("skuId") Long skuId,@PathVariable(name = "skuNum")Integer skuNum,HttpServletRequest request){
        String userIdParam = "";
        String userTempId = request.getHeader("userTempId");
        String userId = request.getHeader("userId");
        System.out.println("userId-----------------"+userId);;
        System.out.println("userTempId-----------------"+userTempId);;
        cartService.addToCart(skuId, skuNum, request);
    }}


