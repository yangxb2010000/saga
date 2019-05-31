package com.tim.sagatransaction.core;

import lombok.Data;

/**
 * @author xiaobing
 */
@Data
public class SagaTransactionConfig {
	/**
	 * 应用Id，用于多个使用用同一个DB的区分
	 */
	private String applicationId;

	/**
	 * 是否异步执行cancel
	 */
	private boolean asyncCancel;

	/**
	 * 是否并发执行Participant的cancel
	 */
	private boolean concurrentCancel;

	/**
	 * 等待并发cancen处理完成的超时时间
	 */
	private int waitConcurrentCancelTimeoutInMs;
}
