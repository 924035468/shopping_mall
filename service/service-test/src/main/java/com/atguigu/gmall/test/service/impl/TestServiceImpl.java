package com.atguigu.gmall.test.service.impl;

import com.atguigu.gmall.test.service.TestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;



    @Override
    public String testLock() {
        String num = stringRedisTemplate.opsForValue().get("num");

        if(StringUtils.isNotBlank(num)){

            Integer i = Integer.parseInt(num);
            i++;
            stringRedisTemplate.opsForValue().set("num",String.valueOf(i));
            System.out.println("目前缓存商品数量为："+i);
            return String.valueOf(i);
        }else {
            stringRedisTemplate.opsForValue().set("num",String.valueOf(0));
            return "0";
        }



    }
}
