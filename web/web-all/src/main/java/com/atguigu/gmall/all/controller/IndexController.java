package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.itemclient.ItemFeignClient;
import com.atguigu.gmall.productclient.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class IndexController {

    @Autowired
    ItemFeignClient itemFeignClient;


    @Autowired
    ProductFeignClient productFeignClient;
    @RequestMapping({"/","index"})
    public String index(ModelMap modelMap){
        //todo
        //首页三级类名获取，使用Stirngapi
        Result result = productFeignClient.getBaseCategoryList();

        modelMap.put("list",result.getData());

        return "index/index";

    }


}
