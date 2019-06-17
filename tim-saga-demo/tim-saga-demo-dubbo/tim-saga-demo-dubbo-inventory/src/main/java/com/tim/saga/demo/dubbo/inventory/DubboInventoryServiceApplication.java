package com.tim.saga.demo.dubbo.inventory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tim.saga.demo.dubbo.inventory.mapper")
public class DubboInventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboInventoryServiceApplication.class, args);
    }
}
