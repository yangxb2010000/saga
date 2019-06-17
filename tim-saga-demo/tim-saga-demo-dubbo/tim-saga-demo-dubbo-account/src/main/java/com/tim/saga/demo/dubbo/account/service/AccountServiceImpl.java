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

package com.tim.saga.demo.dubbo.account.service;

import com.tim.demo.dubbo.account.api.AccountDTO;
import com.tim.demo.dubbo.account.api.AccountService;
import com.tim.saga.demo.dubbo.account.mapper.AccountHistoryMapper;
import com.tim.saga.demo.dubbo.account.mapper.AccountMapper;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author xiaobing
 */
@SuppressWarnings("all")
@Service
public class AccountServiceImpl implements AccountService {

    @Value("${dubbo.application.name}")
    private String serviceName;

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
    public Boolean payment(AccountDTO accountDTO) {
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
    public Boolean cancelPayment(AccountDTO accountDTO) {
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
    public BigDecimal findByUserId(String userId) {
        return accountMapper.findByUserId(userId).getBalance();
    }
}
