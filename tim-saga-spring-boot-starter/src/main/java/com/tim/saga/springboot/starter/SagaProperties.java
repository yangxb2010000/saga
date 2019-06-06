package com.tim.saga.springboot.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xiaobing
 */
@Data
@ConfigurationProperties(prefix = "spring.saga")
@Component
public class SagaProperties {
	/**
	 * 应用Id，用于多个应用用同一个DB的区分，如果为空，会用applicationName
	 */
	private String applicationId;

	/**
	 * 是否异步执行cancel
	 */
	private boolean asyncCancel = false;

	/**
	 * 是否并发执行Participant的cancel
	 */
	private boolean concurrentCancel = false;

	/**
	 * 等待并发cancel处理完成的超时时间
	 */
	private int waitConcurrentCancelTimeoutInMs = 10000;

	/**
	 * 回滚相关配置
	 */
	private RecoverProperties recover;

	/**
	 * cancel 操作的线程池配置
	 */
	private ThreadPoolProperties threadPool;

	/**
	 * 事务持久化配置
	 */
	private RepositoryProperties repository;

	@Data
	public static class RecoverProperties {
		/**
		 * 事务未更新之后多长时间进行recover操作
		 */
		private int recoverDuration;

		/**
		 * 事务最大重试次数
		 */
		private int maxRetryCount;

		/**
		 * 一次任务处理多少个事务
		 */
		private int batchHandleTransactionCount;
	}

	@Data
	public static class RepositoryProperties {
		private JdbcRepositoryProperties jdbc;
	}

	@Data
	public static class JdbcRepositoryProperties {
		private String url;

		private String driverName;

		private String userName;

		private String password;

		private int maxActive;

	}

	@Data
	public static class ThreadPoolProperties {
		/**
		 * corePoolSize
		 */
		private int coreSize = 2;

		/**
		 * maximumPoolSize
		 */
		private int maxSize = 8;
		/**
		 * LinkedBlockingQueue capacity
		 */
		private int blockingDeQueCapacity = 200;
		/**
		 * keepAliveTime in ms
		 */
		private int keepAliveTimeInMs = 30000;
	}
}