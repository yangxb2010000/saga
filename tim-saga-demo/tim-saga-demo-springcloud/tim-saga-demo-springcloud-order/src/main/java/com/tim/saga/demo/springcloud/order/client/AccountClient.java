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

package com.tim.saga.demo.springcloud.order.client;

import com.tim.saga.core.annotation.SagaParticipative;
import com.tim.saga.demo.springcloud.order.dto.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author xiaobing
 */
@FeignClient(value = "account-service")
@SuppressWarnings("all")
public interface AccountClient {

	/**
	 * 用户账户付款
	 *
	 * @param accountDO 实体类
	 * @return true 成功
	 */
	@PostMapping("/account/payment")
	@SagaParticipative(cancelMethod = "cancelPayment")
	Boolean payment(@RequestBody AccountDTO accountDO);

	/**
	 * 用户账户付款
	 *
	 * @param accountDO 实体类
	 * @return true 成功
	 */
	@PostMapping("/account/payment/cancel")
	Boolean cancelPayment(@RequestBody AccountDTO accountDO);

	/**
	 * 获取用户账户信息
	 *
	 * @param userId 用户id
	 * @return AccountDO
	 */
	@PostMapping("/account/findByUserId")
	BigDecimal findByUserId(@RequestParam("userId") String userId);

}
