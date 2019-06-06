package com.tim.saga.core;

import com.tim.saga.core.repository.TransactionRepository;
import lombok.Builder;
import lombok.Data;

/**
 *
 * 封装一些基础的事务操作相关的类，方便上层应用使用，避免上层应用依赖太多的类
 * @author xiaobing
 */
@Data
@Builder
public class SagaApplicationContext {

	private SagaTransactionManager transactionManager;

	private TransactionRepository transactionRepository;

	private SagaConfig sagaConfig;

}
