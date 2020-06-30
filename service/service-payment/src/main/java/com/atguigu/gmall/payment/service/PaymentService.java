package com.atguigu.gmall.payment.service;

import com.atguigu.gmall.model.payment.PaymentInfo;

public interface PaymentService {
    String alipaySubmit(String orderId);



    void updatePayment(PaymentInfo paymentInfo);
}
