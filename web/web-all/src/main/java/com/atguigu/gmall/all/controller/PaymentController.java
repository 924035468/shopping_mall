package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.payment.client.PaymentFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class PaymentController {

    @Autowired
    OrderFeignClient orderFeignClient;

    @Autowired
    PaymentFeignClient paymentFeignClient;



    @RequestMapping("callback/return")
    public String callbackReturn(HttpServletRequest request, ModelMap modelMap){
        // 获得签名并且验证签名
        String sign = request.getParameter("sign");
        // 调用支付服务paymentApi验证签名paymentFeignClient.yanqian(sign);

        String out_trade_no = request.getParameter("out_trade_no");
        String trade_no = request.getParameter("trade_no");
        String trade_status = request.getParameter("trade_status");

        // 接收回调参数，修改支付信息状态
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(request.getQueryString());
        paymentInfo.setTradeNo(trade_no);
        // 根据交易状态更新支付服务的支付结果
        if(true){

            trade_status = PaymentStatus.PAID.toString();
        }
        paymentInfo.setPaymentStatus(trade_status);
        paymentInfo.setOutTradeNo(out_trade_no);
        paymentFeignClient.updatePayment(paymentInfo);


        return "payment/success";

    }

    @RequestMapping("pay.html")
    public String pay(String orderId, ModelMap modelMap){
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId);
        modelMap.put("orderInfo",orderInfo);
        return "payment/pay";
    }



    @RequestMapping("callback/return")
    public String callbackReturn(ModelMap modelMap, HttpServletRequest request){
        return "payment/success";
    }

}
