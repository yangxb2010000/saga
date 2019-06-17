package com.tim.saga.demo.dubbo.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@EnableAutoConfiguration
@MapperScan("com.tim.saga.demo.dubbo.account.mapper")
public class DubboAccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboAccountServiceApplication.class, args);
    }
}
