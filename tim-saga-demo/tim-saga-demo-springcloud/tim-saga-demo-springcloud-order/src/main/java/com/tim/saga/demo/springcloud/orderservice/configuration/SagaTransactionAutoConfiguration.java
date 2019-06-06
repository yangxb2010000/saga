//package com.time.saga.demo.springcloud.orderservice.configuration;
//
//import ch.qos.logback.core.util.ExecutorServiceUtil;
//import com.tim.saga.core.SagaConfig;
//import com.tim.saga.core.SagaTransactionManager;
//import com.tim.saga.core.repository.TransactionRepository;
//import com.tim.saga.core.support.BeanFactory;
//import com.tim.saga.core.support.BeanFactoryBuilder;
//import com.tim.saga.core.transaction.SagaParticipant;
//import com.tim.saga.core.transaction.SagaTransaction;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * @author xiaobing
// */
//@Configuration
//public class SagaTransactionAutoConfiguration {
//
//	@Autowired
//	private ApplicationContext applicationContext;
//
//	@Bean
//	public SagaTransactionManager transactionManager() {
//		SagaConfig sagaConfig = new SagaConfig();
//		sagaConfig.setApplicationId("order-service");
//
//		TransactionRepository transactionRepository = new TransactionRepository() {
//			@Override
//			public int create(SagaTransaction transaction) {
//				return 0;
//			}
//
//			@Override
//			public int updateTransactionStatus(SagaTransaction transaction) {
//				return 0;
//			}
//
//			@Override
//			public int addParticipant(SagaParticipant participant) {
//				System.out.println("SagaTransaction updateTransactionStatus");
//				return 0;
//			}
//
//			@Override
//			public int updateParticipantStatus(SagaParticipant participant) {
//				return 0;
//			}
//
//			@Override
//			public SagaTransaction findByTransactionId(String transactionId) {
//				return null;
//			}
//
//			@Override
//			public List<SagaTransaction> findAllUnmodifiedSince(Date date) {
//				return null;
//			}
//
//			@Override
//			public List<SagaParticipant> getAllParticipant(String transactionId) {
//				return null;
//			}
//		};
//
//		BeanFactoryBuilder.setBeanFactory(new BeanFactory() {
//			@Override
//			public <T> T getBean(Class<T> var1) {
//				return applicationContext.getBean(var1);
//			}
//		});
//
//
//		SagaTransactionManager sagaTransactionManager = new SagaTransactionManager(sagaConfig, transactionRepository, ExecutorServiceUtil.newExecutorService());
//		return sagaTransactionManager;
//	}
//
//
//}
