package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.product.service.BaseSaleAttrService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(description="获取基本的销售属性")
@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class baseSaleAttrController {
    @Autowired
    private BaseSaleAttrService baseSaleAttrService;
    @ApiOperation("获取所有销售属性")
    @GetMapping("baseSaleAttrList")
    public Result getbaseSaleAttrList(){

        List<BaseSaleAttr> list = baseSaleAttrService.list(null);

        return Result.ok(list);

    }

}
