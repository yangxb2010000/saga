package com.tim.saga.demo.dubbo.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@MapperScan("com.tim.saga.demo.dubbo.order.mapper")
//由于基于注解方式的reference不会把referencebean放入beanfactory，所以暂时只支持xml配置的方式
@ImportResource("classpath:spring-dubbo.xml")
public class DubboOrderServiceApplication {
    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(DubboOrderServiceApplication.class, args);

        String[] beanNames = ctx.getBeanDefinitionNames();
        for (String str : beanNames) {
            System.out.println("beanName:" + str);
        }
    }
}
