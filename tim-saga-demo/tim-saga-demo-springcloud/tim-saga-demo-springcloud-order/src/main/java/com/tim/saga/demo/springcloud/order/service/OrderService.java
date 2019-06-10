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

package com.tim.saga.demo.springcloud.order.service;

import com.tim.saga.demo.springcloud.order.entity.Order;

import java.math.BigDecimal;

/**
 * OrderService.
 *
 * @author xiaobing
 */
public interface OrderService {

	/**
	 * 创建订单并且进行扣除账户余额支付，并进行库存扣减操作.
	 *
	 * @param count  购买数量
	 * @param amount 支付金额
	 * @return string
	 */
	String orderPay(Integer count, BigDecimal amount);

	/**
	 * 模拟在订单支付操作中，扣减库存异常.
	 *
	 * @param count  购买数量
	 * @param price  单价
	 * @return string
	 */
	String mockInventoryWithException(Integer count, BigDecimal price);

	/**
	 * 模拟在订单支付操作中，扣减库存时系统宕机.
	 *
	 * @param count  购买数量
	 * @param price 单价
	 * @return string
	 */
	String mockInventoryWithShutdown(Integer count, BigDecimal price);

	/**
	 * 更新订单状态.
	 *
	 * @param order 订单实体类
	 */
	void updateOrderStatus(Order order);
}
