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

package com.tim.saga.demo.springcloud.account.service.impl;

import com.tim.saga.demo.springcloud.account.dto.AccountDTO;
import com.tim.saga.demo.springcloud.account.entity.AccountDO;
import com.tim.saga.demo.springcloud.account.mapper.AccountHistoryMapper;
import com.tim.saga.demo.springcloud.account.mapper.AccountMapper;
import com.tim.saga.demo.springcloud.account.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xiaobing
 */
@Service("accountService")
@SuppressWarnings("all")
public class AccountServiceImpl implements AccountService {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

	@Autowired
	private AccountMapper accountMapper;

	@Autowired
	private AccountHistoryMapper accountHistoryMapper;

	/**
	 * 扣款支付
	 *
	 * @param accountDTO 参数dto
	 * @return true
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean payment(AccountDTO accountDTO) {
		LOGGER.debug("============springcloud执行付款接口===============");
		int res = accountMapper.reduce(accountDTO);
		if (res <= 0) {
			throw new RuntimeException("扣减Inventory库存失败");
		}

		res = accountHistoryMapper.insert(accountDTO.getUserId(), accountDTO.getOrderId(), accountDTO.getAmount());
		if (res <= 0) {
			throw new RuntimeException("添加AccountHistory记录失败");
		}

		return true;
	}

	/**
	 * 扣款支付
	 *
	 * @param accountDTO 参数dto
	 * @return true
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean cancelPayment(AccountDTO accountDTO) {
		LOGGER.debug("============springcloud执行付款接口的cancel操作===============");
		int res = accountHistoryMapper.updateHasRollbacked(accountDTO.getUserId(), accountDTO.getOrderId());
		if (res <= 0) {
			// 更新历史记录失败，说明已经回滚或者不存在，直接返回成功
			return true;
		}

		res = accountMapper.add(accountDTO);
		if (res <= 0) {
			throw new RuntimeException("添加Account余额失败");
		}

		return true;
	}

	/**
	 * 获取用户账户信息
	 *
	 * @param userId 用户id
	 * @return AccountDO
	 */
	@Override
	public AccountDO findByUserId(String userId) {
		return accountMapper.findByUserId(userId);
	}
}
