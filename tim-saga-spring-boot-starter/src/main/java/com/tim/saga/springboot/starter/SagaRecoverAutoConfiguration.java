package com.tim.saga.springboot.starter;

import com.tim.saga.core.SagaApplicationContext;
import com.tim.saga.core.SagaTransactionManager;
import com.tim.saga.core.recovery.SagaRecoveryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author xiaobing
 */
@Configuration
@Import(SagaCoreAutoConfiguration.class)
@ConditionalOnProperty(prefix = "spring.saga.recover", name = "enable", havingValue = "true")
@AutoConfigureAfter(SagaCoreAutoConfiguration.class)
public class SagaRecoverAutoConfiguration {

	@EnableScheduling
	@Configuration
	public static class RecoverAutoConfiguration {
		@Autowired
		SagaRecoveryManager sagaRecoveryManager;

		@ConditionalOnMissingBean(SagaRecoveryManager.class)
		@Bean
		public SagaRecoveryManager sagaRecoveryManager(SagaApplicationContext sagaApplicationContext) {
			return new SagaRecoveryManager(sagaApplicationContext);
		}

		@Scheduled(fixedRate = 5000)
		public void scheduleRecover() {
			System.out.println("starting recover");
			sagaRecoveryManager.startRecover();
		}
	}
}
