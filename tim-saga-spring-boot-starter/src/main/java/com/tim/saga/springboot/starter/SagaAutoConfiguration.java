package com.tim.saga.springboot.starter;

import com.tim.saga.core.SagaTransactionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author xiaobing
 */
@Configuration
@ConditionalOnClass(SagaTransactionManager.class)
@ConditionalOnProperty(prefix = "spring.saga", name = "enable", havingValue = "true", matchIfMissing = true)
@Import({SagaCoreAutoConfiguration.class, SagaRecoverAutoConfiguration.class})
public class SagaAutoConfiguration {

}
