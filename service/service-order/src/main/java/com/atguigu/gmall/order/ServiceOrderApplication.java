package com.atguigu.gmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu.gmall")
@EnableDiscoveryClient
@MapperScan("com.atguigu.gmall.order.mapper")
@EnableFeignClients(basePackages = "com.atguigu.gmall")
public class ServiceOrderApplication {
public static void main(String[] args) {

        SpringApplication.run(ServiceOrderApplication.class,args);
    }
}
