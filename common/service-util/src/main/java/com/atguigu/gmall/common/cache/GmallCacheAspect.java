package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import io.lettuce.core.RedisClient;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class GmallCacheAspect {

        @Autowired
        private RedissonClient redissonClient;
        @Autowired
        private RedisTemplate redisTemplate;


        @Around("@annotation(com.atguigu.gmall.common.cache.GmallCache)")
        public Object CacheAroundAdvice(ProceedingJoinPoint point){
            Object[] args = point.getArgs();
            MethodSignature signature = (MethodSignature) point.getSignature();
            Object  result =null;

            Class returnType = signature.getReturnType();
            GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);

            String prefix = gmallCache.prefix();
            String name = signature.getMethod().getName();


            String key = prefix + Arrays.asList(args).toString();

            result = cacheHit(key,returnType);
            if (result != null) return result;

            RLock lock = redissonClient.getLock(key);

            try {
                boolean b = lock.tryLock(100, 10, TimeUnit.SECONDS);

                if(b){
                    result = point.proceed(args);
                    if(result == null){
                        redisTemplate.opsForValue().set(key, JSON.toJSONString(new Object()),60*60,TimeUnit.SECONDS);
                        return result;

                    }
                    redisTemplate.opsForValue().set(key,JSON.toJSONString(result),60,TimeUnit.SECONDS);

                    return result;
                }else {
                    Thread.sleep(1000);
                    return cacheHit(key,returnType);
                }




            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }finally{
                lock.unlock();
            }




            return result;
        }

    private Object cacheHit(String key,Class returnType) {
        Object result = null;
        result = redisTemplate.opsForValue().get(key);
        if(StringUtils.isNotBlank((String)result)){

            result = JSON.parseObject((String)result, returnType);
            return result;
        }
        return result;
    }
}
