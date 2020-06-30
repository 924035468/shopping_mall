package com.atguigu.gmall.payment.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/payment/")
public class PaymentApiController {
    @Autowired
    PaymentService paymentService;
    @RequestMapping("inner/updatePayment")
    public Result updatePayment(@RequestBody PaymentInfo paymentInfo){
        paymentService.updatePayment(paymentInfo);

        return Result.ok();
    };

    @RequestMapping("alipay/submit/{orderId}")
    public String alipaySubmit(@PathVariable("orderId") String orderId, ModelMap modelMap){

        // 调用service获得支付宝form表单
        String form = paymentService.alipaySubmit(orderId);
        System.out.println(form);
        return form;
    }


}
