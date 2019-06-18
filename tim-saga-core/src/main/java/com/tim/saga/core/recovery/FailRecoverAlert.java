package com.tim.saga.core.recovery;

import com.tim.saga.core.transaction.SagaTransaction;

public interface FailRecoverAlert {

    /**
     * 事务回滚彻底失败的报警
     *
     * @param transaction 事务
     * @param lastError   最后一次回滚的错误
     * @param maxRetries  最大重试次数
     */
    void alertCompleteFail(SagaTransaction transaction, String lastError, int maxRetries);
}
