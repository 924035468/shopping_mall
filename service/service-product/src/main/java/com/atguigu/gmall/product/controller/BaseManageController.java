package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.service.Tags;

import java.util.List;

@Api(description="商品基础属性接口")
@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class BaseManageController {



        @Autowired
        private ManageService manageService;
        @GetMapping("{page}/{limit}")
        public Result index(@PathVariable("page") Long page,
                            @PathVariable("limit") Long limit,
                            @RequestParam Long category3Id
                            ){
            Page<SpuInfo> spuInfoPage = new Page<SpuInfo>(page,limit);
            QueryWrapper<SpuInfo> spuInfoQueryWrapper = new QueryWrapper<>();

            spuInfoQueryWrapper.eq("category3_id",category3Id);
            IPage<SpuInfo> spuInfoIPage = manageService.selectPage(spuInfoPage, spuInfoQueryWrapper);
            return Result.ok(spuInfoIPage);

        }



        @ApiOperation(value = "获取一级目录")
        @GetMapping("getCategory1")
        public Result getCategory1(){
            List<BaseCategory1> list = manageService.getCategory1();
            return Result.ok(list);

        }


    @ApiOperation(value = "获取二级目录")
    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") String category1Id){
        List<BaseCategory2> baseCategory2s = manageService.getCategory2(category1Id);
        return Result.ok(baseCategory2s);
    }


    @ApiOperation(value = "获取三级目录")
    @GetMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable("category2Id") String category2Id){
        List<BaseCategory3> baseCategory3s = manageService.getCategory3(category2Id);
        return Result.ok(baseCategory3s);
    }









}
