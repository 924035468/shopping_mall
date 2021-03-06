package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.HttpClientUtil;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.PaymentWay;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.userclient.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("api/order/")
public class OrderApiController {


    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    OrderInfoService orderInfoService;



    @RequestMapping("inner/getOrderInfo/{orderId}")
    public OrderInfo getOrderInfo(@PathVariable("orderId") String orderId , HttpServletRequest request){
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        return orderInfo;
    }

    @RequestMapping("inner/getTradeNo/{userId}")
    public String getTradeNo(@PathVariable("userId") String userId , HttpServletRequest request){
        if(StringUtils.isEmpty(userId)){
            userId = request.getHeader("userId");
        }
        return orderInfoService.getTradeNo(userId);
    }

    @RequestMapping("auth/submitOrder")
    public Result submitOrder(String tradeNo, HttpServletRequest request){

        String userId = request.getHeader("userId");
        // 对比tradeNo
        boolean b = orderInfoService.checkTradeNo(userId, tradeNo);
        if(b){
            // 生成需要提交
            OrderInfo orderInfo = new OrderInfo();
            // 查询订单详情(购物车)
            // detailArrayList//被选中的购物车集合
            List<OrderDetail> orderDetails = new ArrayList<>();
            List<CartInfo> cartInfos = cartFeignClient.getCartCheckedList(userId);
            StringBuffer tradeBody = new StringBuffer();
            System.out.println("tradeBody1111:"+tradeBody);

            for (CartInfo cartInfo : cartInfos) {
                OrderDetail orderDetail = new OrderDetail();
                // 将购物车数据封装给订单详情
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setOrderPrice(cartInfo.getCartPrice());
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setSkuNum(cartInfo.getSkuNum());

                // 校验当前库存数量是否>购买数量
                // 调用库存系统接口，获得库存查询结果http://localhost:9001/hasStock
                // 通过httpClient，远程调用库存系统的webservice接口
                String stockHttp = "http://localhost:9001/hasStock?skuId="+cartInfo.getSkuId()+"&num="+cartInfo.getSkuNum();
                String stock = HttpClientUtil.doGet(stockHttp);

                if(stock.equals("0")){
                    return Result.fail(ResultCodeEnum.SECKILL_FINISH);
                }

                // 校验当前商品的价格是否=购物车价格
                if(false){
                    return Result.fail("价格不合法");
                }

                orderDetails.add(orderDetail);//
            }

            orderInfo.setOrderDetailList(orderDetails);
            System.out.println("tradeBody2222:"+tradeBody);
            if(tradeBody.toString().length()>100){
                orderInfo.setTradeBody(tradeBody.toString().substring(0,100));
                System.out.println("tradeBody3333:"+tradeBody);
            }else {
                orderInfo.setTradeBody(tradeBody.toString());
                System.out.println("tradeBody4444:"+tradeBody);
            }
            // 页面商品总数量和结算总金额
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String format = sdf.format(new Date());
            String outTradeNo = "ATGUIGU"+ System.currentTimeMillis() + ""+ new Random().nextInt(1000);//外部订单号
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
            orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
            orderInfo.setPaymentWay(PaymentWay.ONLINE.getComment());
            orderInfo.setTradeBody("谷粒订单");
            orderInfo.setTrackingNo("物流单号");
            orderInfo.setUserId(Long.parseLong(userId));
            orderInfo.setImgUrl(cartInfos.get(0).getImgUrl());
            orderInfo.sumTotalAmount();// 核算价格
            orderInfo.setCreateTime(new Date());
            // 封装收货信息
            List<UserAddress> userAddressListByUserId = userFeignClient.findUserAddressListByUserId(userId);
            UserAddress userAddress = userAddressListByUserId.get(0);
            orderInfo.setDeliveryAddress(userAddress.getUserAddress());
            orderInfo.setConsignee(userAddress.getConsignee());
            orderInfo.setConsigneeTel(userAddress.getPhoneNum());

            // 过期时间的计算
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.DATE,1);
            orderInfo.setExpireTime(instance.getTime());

            // 提交订单业务调用
            orderInfo = orderInfoService.saveOrderInfo(orderInfo);
            return Result.ok(orderInfo.getId());
        }else{
            return Result.fail("订单已经提交过");
        }


    }


}
