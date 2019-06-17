package com.tim.saga.demo.dubbo.order.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author xiaobing
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DubboOrderWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboOrderWebApplication.class, args);
    }
}
