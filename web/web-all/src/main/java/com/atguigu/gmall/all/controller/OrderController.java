package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.userclient.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping

public class OrderController {

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    CartFeignClient cartFeignClient;


    @RequestMapping("trade.html")
    public String trade(ModelMap modelMap,
                        @RequestHeader(name = "userId") String userId){

        List<UserAddress> userAddressListByUserId =
                userFeignClient.findUserAddressListByUserId(userId);
        modelMap.put("userAddressList", userAddressListByUserId);
        List<OrderDetail> orderDetails = cartFeignClient.getIsCheckedCartList(userId);
        modelMap.put("detailArrayList", orderDetails);
        //tradeNo
        //totalAmount
        //totalNum


        BigDecimal bigDecima = new BigDecimal(0);
        BigDecimal totalNum = new BigDecimal(0);;

        BigDecimal totalAmount1 = new BigDecimal(0);
        BigDecimal totalAmount2 = new BigDecimal(0);;


        for (OrderDetail orderDetail:
        orderDetails) {

            totalNum = new BigDecimal(orderDetail.getSkuNum()).add(bigDecima);

            bigDecima = totalNum;

            totalAmount2 = (orderDetail.getOrderPrice().multiply(totalNum)).add(totalAmount1);

            totalAmount1 = totalAmount2;

        }
        //modelMap.put("tradeNo", tradeNo);

        modelMap.put("totalAmount", totalAmount1);
        modelMap.put("totalNum", totalNum);

        return "order/trade";
    }
}


