package com.atguigu.gmall.test.testThread;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class TestMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        new Thread(new FutureTask<BigDecimal>(new Callable<BigDecimal>() {
            @Override
            public BigDecimal call() throws Exception {
                return null;
            }
        })).start();


        FutureTask<SkuInfo> skuInfoFutureTask = new FutureTask<>(new SkuCallable());

        new Thread(skuInfoFutureTask).start();


        FutureTask<BaseCategoryView> baseCategoryViewFutureTask = new FutureTask<>(new CategoryCallable());

        new Thread(baseCategoryViewFutureTask).start();

        FutureTask<Long> longFutureTask = new FutureTask<>(new PriceCallable());
        new Thread(longFutureTask).start();



        SkuInfo skuInfo = skuInfoFutureTask.get();


        BaseCategoryView baseCategoryView = baseCategoryViewFutureTask.get();
        Long aLong = longFutureTask.get();


    }



}
