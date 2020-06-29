package com.atguigu.gmall.cart.Service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.Service.CartService;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.productclient.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartInfoMapper cartInfoMapper;
    @Autowired
    ProductFeignClient productFeignClient;


    @Autowired
    RedissonClient redisson;



    @Autowired
    RedisTemplate redisTemplate;

    private CartKey cartKey = new CartKey();

    public String getUserCartKey(String userId){

        return RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
    }


    public String getUserIdOruserTempId(HttpServletRequest request){



        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
        if(StringUtils.isNoneEmpty(userId)&&StringUtils.isEmpty(userTempId)){
            //登录账号
            cartKey.setUserId(userId);
            cartKey.setLogin(true);
            cartKey.setMerge(false);
            cartKey.setTemplogin(false);
        }

        if(StringUtils.isEmpty(userId)&&StringUtils.isNoneEmpty(userTempId)){
            //没有登录账号，有临时账号
            cartKey.setUserId(userTempId);
            cartKey.setTemplogin(true);
            cartKey.setLogin(false);
            cartKey.setMerge(false);
        }
        if(StringUtils.isNoneEmpty(userId) &&StringUtils.isNoneEmpty(userTempId)){

            //没有临时id 也没有用户id

            cartKey.setUserId(userId);
            cartKey.setTemplogin(true);
            cartKey.setLogin(true);
            cartKey.setMerge(true);
        }

        return cartKey.getUserId();

    }

    public void mergeCart(HttpServletRequest request){
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
       // RMap<Object, Object> mapByUserId = redisson.getMap("user:" + userId + ":cart");
        RMap<Object, Object> mapByUserTempId = redisson.getMap("user:" + userTempId + ":cart");
        //List<CartInfo> cartInfos = new ArrayList<>();

        redisTemplate.opsForHash().putAll("user:"+userId+":cart", mapByUserTempId);

    }

    @Override
    public  void addToCart(Long skuId, Integer skuNum,
                          HttpServletRequest request
                          ){
        String userId = getUserIdOruserTempId(request);
        String userTempId = request.getHeader("userTempId");
        boolean merge = cartKey.isMerge();
        if(cartKey.isMerge()){
            mergeCart(request);
        }


        if(null != userId && userId.length()>0){
            QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
            cartInfoQueryWrapper.eq("user_id", userId);
            cartInfoQueryWrapper.eq("sku_id", skuId);
            CartInfo cartInfo = cartInfoMapper.selectOne(cartInfoQueryWrapper);
            //数据库存在用户 用户已经添加过一次该商品，更新，数量加
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if(cartInfo != null){
                cartInfo.setSkuNum(cartInfo.getSkuNum()+ skuNum);
                BigDecimal price = skuInfo.getPrice();

                cartInfo.setSkuPrice(price);
                cartInfoMapper.updateById(cartInfo);
            }else {


                cartInfo = new CartInfo();
                cartInfo.setSkuNum(skuNum);
                cartInfo.setCartPrice(skuInfo.getPrice().multiply(new BigDecimal(skuNum)));
                cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
                cartInfo.setIsChecked(1);
                cartInfo.setSkuId(skuInfo.getId());
                cartInfo.setSkuName(skuInfo.getSkuName());
                cartInfo.setUserId(userId);

                BigDecimal price = skuInfo.getPrice();

                cartInfo.setSkuPrice(price);

                cartInfoMapper.insert(cartInfo);


            }

            redisTemplate.opsForHash().put(getUserCartKey(userId), cartInfo.getSkuId()+"", cartInfo);
        }




        //未完成
    }

    @Override
    public List<CartInfo> cartList(String userIdParam) {
       // Map entries = redisTemplate.opsForHash().entries(getUserCartKey(userIdParam));
        List<CartInfo>  CartInfos = redisTemplate.opsForHash().values(getUserCartKey(userIdParam));


        return CartInfos;
    }

    @Override
    public void checkCart(Long skuId, Integer check, HttpServletRequest request) {
        String userId = request.getHeader("userId");

        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
        cartInfoQueryWrapper.eq("user_id", userId);
        cartInfoQueryWrapper.eq("sku_id", skuInfo.getId());


        CartInfo cartInfo = cartInfoMapper.selectOne(cartInfoQueryWrapper);
        cartInfo.setIsChecked(check);
        cartInfoMapper.update(cartInfo, cartInfoQueryWrapper);


        // 同步缓存
        loadCartCache(userId);


    }

    @Override
    public List<OrderDetail> getIsCheckedCartList(String userId) {

        List<CartInfo> cartInfos = redisTemplate.opsForHash().values(getUserCartKey(userId));


        List<OrderDetail> orderDetails = null;

        orderDetails = cartInfos.stream().map(cartInfo -> {

            OrderDetail  orderDetail = new OrderDetail();
            if(cartInfo.getIsChecked() == 1){
                // 将购物车数据封装给订单详情
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setOrderPrice(cartInfo.getCartPrice());
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setSkuNum(cartInfo.getSkuNum());
            }
            return orderDetail;
        }) .collect(Collectors.toList());

        List<OrderDetail> collect = orderDetails.stream().filter(orderDetail -> orderDetail.getSkuId() != null).collect(Collectors.toList());

        return collect;
    }


    public List<CartInfo> loadCartCache(String userId){
        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<CartInfo> cartInfos = cartInfoMapper.selectList(queryWrapper);
        // 同步缓存
        if(null!=cartInfos){
            HashMap<String, CartInfo> map = new HashMap<>();
            for (CartInfo cartInfo : cartInfos) {

                // 没从重新加载缓存的时候，需要重新查询sku的当前价格，更新到购物车
                SkuInfo skuInfo = productFeignClient.getSkuInfo(cartInfo.getSkuId());
                cartInfo.setSkuPrice(skuInfo.getPrice());

                map.put(cartInfo.getSkuId()+"",cartInfo);
            }
            redisTemplate.opsForHash().putAll(getUserCartKey(userId),map);
        }
        return cartInfos;
    }
}


@Data
class CartKey {


    private  String userId;
    private  boolean login;
    private boolean templogin;
    private boolean merge;

}

