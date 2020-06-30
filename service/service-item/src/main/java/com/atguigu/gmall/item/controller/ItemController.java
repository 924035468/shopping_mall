package com.atguigu.gmall.item.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("item/")

public class ItemController {




    @RequestMapping("test")
    public String test(){




        return "test-zfp";

    }


}
