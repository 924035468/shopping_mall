package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.productclient.ProductFeignClient;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.List;

@Controller
@RequestMapping
@Slf4j
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    ProductFeignClient productFeignClient;

    @RequestMapping("cart.html")
    public String cart(HttpServletRequest request,
                          ModelMap modelMap){


        String userId = request.getHeader("userId");

        if(StringUtils.isEmpty(userId)){
            String requestURI1 = request.getRequestURI();//获取访问路径: 项目名+servlet
            System.out.println("requestURL1:"+requestURI1);
            return "redirect:http://passport.gmall.com/login.html?originUrl=http://cart.gmall.com/"+requestURI1;
        }



        Result<List> result= cartFeignClient.cartList();

        List<CartInfo> cartInfos = result.getData();
        modelMap.put("data",cartInfos);

     /*   Result<List> result =  cartFeignClient.cartList();
        List<CartInfo> cartInfos = result.getData();
        modelMap.put("data", cartInfos);*/
       /* SkuInfo skuInfo = productFeignClient.getSkuInfo(cartInfo.getSkuId());


        System.out.println(skuInfo);*/

        return "cart/index";

    }


    @RequestMapping("addCart.html")
    public String addCart(CartInfo cartInfo, HttpServletRequest request,
                          ModelMap modelMap){
        SkuInfo skuInfo = productFeignClient.getSkuInfo(cartInfo.getSkuId());
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
        System.out.println("userId-----------------"+userId);;
        System.out.println("userTempId-----------------"+userTempId);;
        System.out.println(skuInfo);
        modelMap.put("skuInfo", skuInfo);

        cartFeignClient.addToCart(cartInfo.getSkuId(),cartInfo.getSkuNum());
        return "cart/addCart";

    }


}
