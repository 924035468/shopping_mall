package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import io.swagger.annotations.Api;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description="info基础属性接口")
@RequestMapping("admin/product/")
@RestController
@CrossOrigin
public class BaseAttrInfoController {

    @Autowired
    private BaseAttrInfoService baseAttrInfoService;


    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValue(@PathVariable("attrId") Long attrId){

        List<BaseAttrValue> baseAttrValues=baseAttrInfoService.getAttrVlue(attrId);
        return Result.ok(baseAttrValues);

    }


    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getAttrInfoList(@PathVariable("category1Id") String category1Id,
                                   @PathVariable("category2Id") String category2Id,
                                   @PathVariable("category3Id") String category3Id
                                   ){

        List<BaseAttrInfo> attrInfolist =  baseAttrInfoService.getArrtInfo(category1Id,category2Id,category3Id);
        return Result.ok(attrInfolist);

    }

    @RequestMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){

        baseAttrInfoService.saveAttrInfo(baseAttrInfo);

        return Result.ok();
    }
}
