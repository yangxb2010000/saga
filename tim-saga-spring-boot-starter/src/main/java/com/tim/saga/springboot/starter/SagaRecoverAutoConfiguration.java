package com.tim.saga.springboot.starter;

import com.tim.saga.core.SagaApplicationContext;
import com.tim.saga.core.SagaTransactionManager;
import com.tim.saga.core.recovery.FailRecoverAlert;
import com.tim.saga.core.recovery.SagaRecoveryManager;
import com.tim.saga.core.recovery.alert.DingDingFailRecoverAlert;
import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Optional;

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
        public SagaRecoveryManager sagaRecoveryManager(SagaApplicationContext sagaApplicationContext,
                                                       Optional<FailRecoverAlert> failRecoverAlert) {
            SagaRecoveryManager recoveryManager = new SagaRecoveryManager(sagaApplicationContext);

            failRecoverAlert.ifPresent(alert -> recoveryManager.setFailRecoverAlert(alert));

            return recoveryManager;
        }


        @ConditionalOnProperty(prefix = "spring.saga.recover.fail-recover-alert.dingding", name = "web-hook-url")
        @ConditionalOnMissingBean(FailRecoverAlert.class)
        @Bean
        public FailRecoverAlert dingDingFailRecoverAlert(SagaProperties sagaProperties) {
            String webHookUrl = sagaProperties.getRecover().getFailRecoverAlert().getDingding().getWebHookUrl();
            return new DingDingFailRecoverAlert(webHookUrl);
        }

        @Scheduled(fixedRate = 5000)
        public void scheduleRecover() {
            sagaRecoveryManager.startRecover();
        }
    }
}
