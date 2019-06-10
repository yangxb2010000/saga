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

package com.tim.saga.demo.springcloud.order.service.impl;

import com.tim.saga.demo.springcloud.order.entity.Order;
import com.tim.saga.demo.springcloud.order.enums.OrderStatusEnum;
import com.tim.saga.demo.springcloud.order.mapper.OrderMapper;
import com.tim.saga.demo.springcloud.order.service.OrderService;
import com.tim.saga.demo.springcloud.order.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;


/**
 * @author xiaobing
 */
@Service("orderService")
@SuppressWarnings("all")
public class OrderServiceImpl implements OrderService {

	/**
	 * logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

	private final OrderMapper orderMapper;

	private final PaymentService paymentService;

	@Autowired(required = false)
	public OrderServiceImpl(OrderMapper orderMapper, PaymentService paymentService) {
		this.orderMapper = orderMapper;
		this.paymentService = paymentService;
	}

	@Override
	public String orderPay(Integer count, BigDecimal price) {
		final Order order = buildOrder(count, price);
		final int rows = orderMapper.save(order);

		if (rows > 0) {
			paymentService.makePayment(order);
			return "success";
		} else {
			return "fail";
		}
	}

	/**
	 * 模拟在订单支付操作中，库存在try阶段中的库存异常
	 *
	 * @param count  购买数量
	 * @param amount 支付金额
	 * @return string
	 */
	@Override
	public String mockInventoryWithException(Integer count, BigDecimal amount) {
		final Order order = buildOrder(count, amount);
		final int rows = orderMapper.save(order);

		if (rows > 0) {
			paymentService.mockPaymentInventoryWithException(order);
		}

		return "success";
	}

	/**
	 * 模拟在订单支付操作中，扣库存阶段系统宕机
	 *
	 * @param count  购买数量
	 * @param amount 支付金额
	 * @return string
	 */
	@Override
	public String mockInventoryWithShutdown(Integer count, BigDecimal amount) {
		final Order order = buildOrder(count, amount);
		final int rows = orderMapper.save(order);

		if (rows > 0) {
			paymentService.mockPaymentInventoryWithShutdown(order);
		}

		return "success";
	}


	@Override
	public void updateOrderStatus(Order order) {
		orderMapper.update(order);
	}

	private Order buildOrder(Integer count, BigDecimal price) {
		LOGGER.debug("构建订单对象");
		Order order = new Order();
		order.setCreateTime(new Date());
		order.setNumber(UUID.randomUUID().toString());
		//demo中的表里只有商品id为 1的数据
		order.setProductId(1L);
		order.setStatus(OrderStatusEnum.NOT_PAY.getCode());
		order.setTotalAmount(price.multiply(new BigDecimal(count)));
		order.setCount(count);
		//demo中 表里面存的用户id为1
		order.setUserId(1L);
		return order;
	}
}
