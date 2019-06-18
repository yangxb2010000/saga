package com.tim.saga.springboot.starter;

import com.esotericsoftware.kryo.Kryo;
import com.tim.saga.core.SagaApplicationContext;
import com.tim.saga.core.SagaConfig;
import com.tim.saga.core.SagaTransactionManager;
import com.tim.saga.core.interceptor.SagaParticipativeAspect;
import com.tim.saga.core.interceptor.SagaTransactionalAspect;
import com.tim.saga.core.recovery.FailRecoverAlert;
import com.tim.saga.core.repository.TransactionRepository;
import com.tim.saga.core.serializer.ObjectSerializer;
import com.tim.saga.core.serializer.imp.KyroSerializer;
import com.tim.saga.core.support.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaobing
 */
@Configuration
@ConditionalOnClass(SagaTransactionManager.class)
@Import({SagaRepositoryAutoConfiguration.class, SagaConfigAutoConfiguration.class})
public class SagaCoreAutoConfiguration {
	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	@ConditionalOnMissingBean(SagaParticipativeAspect.class)
	public SagaParticipativeAspect sagaParticipativeAspect(SagaApplicationContext sagaApplicationContext) {
		return new SagaParticipativeAspect(sagaApplicationContext);
	}

	@Bean
	@ConditionalOnMissingBean(SagaTransactionalAspect.class)
	public SagaTransactionalAspect sagaTransactionalAspect(SagaApplicationContext sagaApplicationContext) {
		return new SagaTransactionalAspect(sagaApplicationContext);
	}

	@Bean
	@ConditionalOnMissingBean(SagaApplicationContext.class)
	public SagaApplicationContext sagaApplicationContext(SagaTransactionManager sagaTransactionManager,
														 SagaConfig sagaConfig,
														 TransactionRepository transactionRepository) {
		SagaApplicationContext.SagaApplicationContextBuilder builder = SagaApplicationContext.builder()
				.sagaConfig(sagaConfig)
				.transactionManager(sagaTransactionManager)
				.transactionRepository(transactionRepository);

		return builder.build();
	}

	@Bean
	@ConditionalOnMissingBean(BeanFactory.class)
	public BeanFactory beanFactory() {
		return new BeanFactory() {
			@Override
			public <T> T getBean(Class<T> clazz) {
				return applicationContext.getBean(clazz);
			}
		};
	}

	@Bean
	@ConditionalOnMissingBean(SagaTransactionManager.class)
	public SagaTransactionManager sagaTransactionManager(SagaConfig sagaConfig,
	                                                     TransactionRepository transactionRepository,
	                                                     BeanFactory beanFactory) {
		ExecutorService executorService = null;
		if (sagaConfig.isConcurrentCancel()
				|| sagaConfig.isAsyncCancel()) {
			SagaConfig.ThreadPoolConfig threadPoolConfig = sagaConfig.getThreadPoolConfig();
			Assert.notNull(threadPoolConfig, "ThreadPoolConfig can not be null if async or concurrent cancel is true");

			executorService = new ThreadPoolExecutor(threadPoolConfig.getCoreSize(),
					threadPoolConfig.getMaxSize(),
					threadPoolConfig.getKeepAliveTimeInMs(),
					TimeUnit.MILLISECONDS,
					new LinkedBlockingDeque<>(threadPoolConfig.getBlockingDeQueCapacity()),
					new CustomizableThreadFactory("tim-saga-cancel-"));

		}


		return new SagaTransactionManager(sagaConfig, transactionRepository, executorService, beanFactory);
	}




	@Configuration
	@ConditionalOnMissingBean(ObjectSerializer.class)
	@ConditionalOnClass(Kryo.class)
	public static class SerializerAutoConfiguration {
		@Bean
		public ObjectSerializer objectSerializer() {
			return new KyroSerializer();
		}
	}
}
