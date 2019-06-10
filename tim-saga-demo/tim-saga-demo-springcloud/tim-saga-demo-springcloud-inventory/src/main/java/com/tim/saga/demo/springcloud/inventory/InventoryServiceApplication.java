package com.tim.saga.demo.springcloud.inventory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author xiaobing
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.tim.saga.demo.springcloud.inventory.mapper")
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}


}
