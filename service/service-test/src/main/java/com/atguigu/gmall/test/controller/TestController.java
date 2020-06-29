package com.atguigu.gmall.test.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.test.service.TestService;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Api(tags="测试接口")
public class TestController {

    public static void main(String[] args) {
        String replace = UUID.randomUUID().toString();

        System.out.println(replace);

    }

    @Autowired
    private TestService testService;
    @GetMapping("testLock")
    public Result testLock(){
        String s =testService.testLock();
        return Result.ok(s);
    }

}
