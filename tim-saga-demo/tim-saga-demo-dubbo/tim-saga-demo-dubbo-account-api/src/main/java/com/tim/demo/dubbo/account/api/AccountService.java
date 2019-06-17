package com.tim.demo.dubbo.account.api;

import com.tim.saga.core.annotation.SagaParticipative;

import java.math.BigDecimal;

public interface AccountService {

    /**
     * 用户账户付款
     *
     * @param accountDO 实体类
     * @return true 成功
     */
    @SagaParticipative(cancelMethod = "cancelPayment")
    Boolean payment(AccountDTO accountDO);

    /**
     * 用户账户付款
     *
     * @param accountDO 实体类
     * @return true 成功
     */
    Boolean cancelPayment(AccountDTO accountDO);

    /**
     * 获取用户账户信息
     *
     * @param userId 用户id
     * @return AccountDO
     */
    BigDecimal findByUserId(String userId);

}
