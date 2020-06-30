package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class OrderInfoServiceImpl implements OrderInfoService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    OrderInfoMapper orderInfoMapper;


    @Autowired
    OrderDetailMapper orderDetailMapper;


    @Override
    public String getTradeNo(String userId) {
        // 定义key
        String tradeNoKey = "user:"+ userId + ":tradeNo";

        // 定义一个流水号
        String tradeNoValue = UUID.randomUUID().toString().replace("-", "");

        // 进入redis
        redisTemplate.opsForValue().set(tradeNoKey,tradeNoValue,15*60, TimeUnit.SECONDS);

        return tradeNoValue;
    }

    @Override
    public OrderInfo getOrderInfo(String orderId) {

        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);

        QueryWrapper<OrderDetail> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("order_id",orderId);

        List<OrderDetail> orderDetails = orderDetailMapper.selectList(queryWrapper);

        orderInfo.setOrderDetailList(orderDetails);


        return orderInfo;
    }

    @Override
    public boolean checkTradeNo(String userId, String tradeNo) {
        boolean b = false;
        // 定义key
        String tradeNoKey = "user:"+ userId + ":tradeNo";
        String tradeNoFromCache = (String)redisTemplate.opsForValue().get(tradeNoKey);
//
//        if(!StringUtils.isEmpty(tradeNoFromCache)&&tradeNo.equals(tradeNoFromCache)){
//            b = true;
//            deleteTradeNo(userId);
//        }

        DefaultRedisScript<Long> luaScript = new DefaultRedisScript<>();
        luaScript.setResultType(Long.class);

        luaScript.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
        Long execute = (Long)redisTemplate.execute(luaScript, Arrays.asList(tradeNoKey), tradeNo);// 1成功 0失败

        if(execute==1){
            b = true;
        }

        return b;
    }

    @Override
    public OrderInfo saveOrderInfo(OrderInfo orderInfo) {
        orderInfoMapper.insert(orderInfo);
        Long id = orderInfo.getId();

        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();

        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insert(orderDetail);
        }

        return orderInfo;
    }
}
