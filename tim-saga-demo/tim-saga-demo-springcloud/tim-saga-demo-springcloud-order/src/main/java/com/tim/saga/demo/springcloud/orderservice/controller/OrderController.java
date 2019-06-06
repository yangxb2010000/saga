package com.tim.saga.demo.springcloud.orderservice.controller;

import com.tim.saga.core.annotation.SagaTransactional;
import com.tim.saga.demo.springcloud.orderservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaobing
 */
@RestController
@RequestMapping(path = "/order")
public class OrderController {
	@Autowired
	private InventoryService inventoryService;

	@RequestMapping(method = {RequestMethod.GET}, path = "/")
	@SagaTransactional(name = "createOrder")
	public void createOrder() {

		System.out.println("order creating");

		System.out.println("reduce inventory" + inventoryService.reduceInventory(1, 100));


		System.out.println("order created");
	}

	@RequestMapping(method = {RequestMethod.GET}, path = "/fail")
	@SagaTransactional(name = "")
	public void failCreateOrder() {
		System.out.println("reduce inventory" + inventoryService.reduceInventory(1, 100));

		System.out.println("reduce inventory" + inventoryService.failedReduceInventory(2, 50));
	}
}
