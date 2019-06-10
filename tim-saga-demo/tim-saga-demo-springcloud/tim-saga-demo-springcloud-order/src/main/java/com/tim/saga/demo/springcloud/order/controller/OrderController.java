/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tim.saga.demo.springcloud.order.controller;

import com.tim.saga.demo.springcloud.order.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author xiaobing
 */
@RestController
@RequestMapping("/order")
@SuppressWarnings("all")
public class OrderController {

	private final OrderService orderService;

	@Autowired
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping(value = "/orderPay")
	@ApiOperation(value = "订单支付接口（注意这里模拟的是创建订单并进行支付扣减库存等操作）")
	public String orderPay(@RequestParam(value = "count") Integer count,
	                       @RequestParam(value = "price") BigDecimal price) {
		return orderService.orderPay(count, price);
	}

	@PostMapping(value = "/mockInventoryWithException")
	@ApiOperation(value = "模拟下单付款操作在扣库存阶段异常，此时账户系统和订单状态会回滚，达到数据的一致性（注意:这里模拟的是系统异常，或者rpc异常）")
	public String mockInventoryWithException(@RequestParam(value = "count") Integer count,
	                                         @RequestParam(value = "price") BigDecimal price) {
		return orderService.mockInventoryWithException(count, price);
	}

	@PostMapping(value = "/mockInventoryWithShutdown")
	@ApiOperation(value = "模拟下单付款操作在扣库存阶段系统宕机，此时账户系统和订单状态会在定时任务的作用下回滚，达到数据的一致性")
	public String mockInventoryWithShutDown(@RequestParam(value = "count") Integer count,
	                                        @RequestParam(value = "price") BigDecimal price) {
		return orderService.mockInventoryWithShutdown(count, price);
	}
}
