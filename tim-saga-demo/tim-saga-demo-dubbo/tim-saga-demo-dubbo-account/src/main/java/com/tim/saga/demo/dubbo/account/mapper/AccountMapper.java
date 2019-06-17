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

package com.tim.saga.demo.dubbo.account.mapper;

import com.tim.demo.dubbo.account.api.AccountDTO;
import com.tim.saga.demo.dubbo.account.entity.AccountDO;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * The interface Account mapper.
 *
 * @author xiaobing
 */
@SuppressWarnings("all")
public interface AccountMapper {

	/**
	 * Update int.
	 *
	 * @param accountDTO the account dto
	 * @return the int
	 */
	@Update("update account set balance = balance - #{amount}, update_time = now()" +
			" where user_id =#{userId} and balance > #{amount}  ")
	int reduce(AccountDTO accountDTO);

	/**
	 * Update int.
	 *
	 * @param accountDTO the account dto
	 * @return the int
	 */
	@Update("update account set balance = balance + #{amount}, update_time = now()" +
			" where user_id =#{userId} ")
	int add(AccountDTO accountDTO);

	/**
	 * Find by user id account do.
	 *
	 * @param userId the user id
	 * @return the account do
	 */
	@Select("select id,user_id,balance from account where user_id =#{userId} limit 1")
	AccountDO findByUserId(String userId);
}
