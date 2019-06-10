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

/**
 * PaymentService.
 *
 * @author xiaobing
 */
public interface PaymentService {

	/**
	 * 订单支付.
	 *
	 * @param order 订单实体
	 */
	void makePayment(Order order);

	/**
	 * mock订单支付的时候库存异常.
	 *
	 * @param order 订单实体
	 */
	void mockPaymentInventoryWithException(Order order);

	/**
	 * mock订单支付的时候 扣库存阶段宕机.
	 *
	 * @param order 订单实体
	 */
	void mockPaymentInventoryWithShutdown(Order order);


}
