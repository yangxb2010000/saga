package com.tim.saga.demo.springcloud.inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaobing
 */
@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@RequestMapping("/reduceinventory")
	public String reduceInventory(@RequestParam(name = "productId") int productId, @RequestParam(name = "count") int count) {
		return "reduce inventory for product: " + productId + ",count: " + count;
	}

	@RequestMapping("/cancelreduceinventory")
	public String cancelReduceInventory(@RequestParam(name = "productId") int productId, @RequestParam(name = "count") int count) {
		return "cancel reduce inventory for product: " + productId + ",count: " + count;
	}

	@RequestMapping("/failedreduceinventory")
	public String failedReduceInventory(@RequestParam(name = "productId") int productId, @RequestParam(name = "count") int count) {
		throw new RuntimeException("failed to ReduceInventory");
//		return "reduce inventory for product: " + productId + ",count: " + count;
	}

}
