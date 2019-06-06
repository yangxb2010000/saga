package com.tim.saga.springboot.starter;

import com.tim.saga.core.SagaConfig;
import com.tim.saga.core.SagaTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author xiaobing
 */
@Configuration
@ConditionalOnClass(SagaTransactionManager.class)
@EnableConfigurationProperties(SagaProperties.class)
public class SagaConfigAutoConfiguration {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	@ConditionalOnMissingBean(SagaConfig.class)
	public SagaConfig sagaConfig(SagaProperties properties) {
		SagaConfig.SagaConfigBuilder sagaConfigBuilder = SagaConfig.builder()
				.applicationId(properties.getApplicationId())
				.asyncCancel(properties.isAsyncCancel())
				.concurrentCancel(properties.isConcurrentCancel())
				.waitConcurrentCancelTimeoutInMs(properties.getWaitConcurrentCancelTimeoutInMs());

		if (properties.getRecover() != null) {
			sagaConfigBuilder.recoverConfig(SagaConfig.RecoverConfig
					.builder()
					.recoverDuration(properties.getRecover().getRecoverDuration())
					.maxRetryCount(properties.getRecover().getMaxRetryCount())
					.build());
		}

		if (properties.getThreadPool() != null) {
			sagaConfigBuilder.threadPoolConfig(SagaConfig.ThreadPoolConfig
					.builder()
					.coreSize(properties.getThreadPool().getCoreSize())
					.maxSize(properties.getThreadPool().getMaxSize())
					.blockingDeQueCapacity(properties.getThreadPool().getBlockingDeQueCapacity())
					.keepAliveTimeInMs(properties.getThreadPool().getKeepAliveTimeInMs())
					.build());
		}

		SagaConfig sagaConfig = sagaConfigBuilder.build();

		if (StringUtils.isEmpty(sagaConfig.getApplicationId())) {
			sagaConfig.setApplicationId(applicationContext.getId());
		}

		if (StringUtils.isEmpty(sagaConfig.getApplicationId())) {
			throw new IllegalArgumentException("Application id for SagaConfig cannot be null, consider set spring application name or set saga applicationId ");
		}

		return sagaConfig;
	}
}
