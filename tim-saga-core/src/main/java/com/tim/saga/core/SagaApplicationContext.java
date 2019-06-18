package com.tim.saga.core;

import com.tim.saga.core.recovery.FailRecoverAlert;
import com.tim.saga.core.repository.TransactionRepository;
import lombok.Builder;
import lombok.Data;

/**
 * 封装一些基础的事务操作相关的类，方便上层应用使用，避免上层应用依赖太多的类
 *
 * @author xiaobing
 */
@Data
@Builder
public class SagaApplicationContext {

    /**
     * 事务管理器
     */
    private SagaTransactionManager transactionManager;

    /**
     * 事务持久化
     */
    private TransactionRepository transactionRepository;

    /**
     * 事务的各种配置
     */
    private SagaConfig sagaConfig;

}
