package com.tim.saga.demo.springcloud.orderservice.service;

import com.tim.saga.core.annotation.SagaParticipative;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author xiaobing
 */
@FeignClient(name = "inventory-service")
public interface InventoryService {
	@RequestMapping("/reduceinventory")
	@SagaParticipative(cancelMethod = "cancelReduceInventory")
	String reduceInventory(@RequestParam(name = "productId") int productId, @RequestParam(name = "count") int count);

	@RequestMapping("/cancelreduceinventory")
	String cancelReduceInventory(@RequestParam(name = "productId") int productId, @RequestParam(name = "count") int count);

	@RequestMapping("/failedreduceinventory")
	@SagaParticipative(cancelMethod = "cancelReduceInventory")
	String failedReduceInventory(@RequestParam(name = "productId") int productId, @RequestParam(name = "count") int count);
}
