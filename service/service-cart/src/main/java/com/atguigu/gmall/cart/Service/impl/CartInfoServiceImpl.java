package com.atguigu.gmall.cart.Service.impl;

import com.atguigu.gmall.cart.Service.CartInfoService;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.productclient.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class CartInfoServiceImpl implements CartInfoService {
    @Autowired
    CartInfoMapper cartInfoMapper;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    RedisTemplate redisTemplate;
    public String getUserCartKey(String userId){

        return RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX;
    }

    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        List<CartInfo> cartInfos = new ArrayList<>();
        cartInfos = redisTemplate.opsForHash().values(getUserCartKey(userId));
        if(null==cartInfos||cartInfos.size()==0){
            cartInfos = loadCartCache(userId);
        }
        Iterator<CartInfo> iterator = cartInfos.iterator();
        while(iterator.hasNext()){
            CartInfo next = iterator.next();
            if(new BigDecimal(next.getIsChecked()+"").compareTo(new BigDecimal("0"))==0){
                iterator.remove();
            }
        }
        return cartInfos;
    }


    public List<CartInfo> loadCartCache(String userId) {
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
