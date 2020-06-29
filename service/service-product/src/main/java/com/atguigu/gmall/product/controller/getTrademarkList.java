package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "获取品牌")

@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class getTrademarkList {
    @Autowired
    private BaseTrademarkService baseTrademarkService;







    @GetMapping("baseTrademark/getTrademarkList")
    public Result getgetTrademarkPage(){
        List<BaseTrademark> list = baseTrademarkService.list(null);

        return  Result.ok(list);
    }

    @GetMapping("baseTrademark/{page}/{limit}")
    public Result getgetTrademarkPage(
            @PathVariable("page") Long  page,
            @PathVariable("limit")Long  limit

    ){

        Page<BaseTrademark> baseTrademarkPage = new Page<BaseTrademark>(page, limit);
//        IPage<BaseTrademark> trademarkPage = baseTrademarkService.page(baseTrademarkPage, null);
        IPage<BaseTrademark> trademarkPage = baseTrademarkService.getPage(baseTrademarkPage);
        return Result.ok(trademarkPage);
    }
}
