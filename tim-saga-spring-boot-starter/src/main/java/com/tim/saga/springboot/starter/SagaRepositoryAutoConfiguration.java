package com.tim.saga.springboot.starter;

import com.alibaba.druid.pool.DruidDataSource;
import com.tim.saga.core.repository.TransactionRepository;
import com.tim.saga.core.repository.impl.JdbcTransactionRepository;
import com.tim.saga.core.serializer.ObjectSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author xiaobing
 */
@Configuration
@ConditionalOnClass(TransactionRepository.class)
@Import(SagaRepositoryAutoConfiguration.DruidDataSourceAutoConfiguration.class)
public class SagaRepositoryAutoConfiguration {

	@ConditionalOnProperty(prefix = "spring.saga.repository.jdbc", name = "url")
	@ConditionalOnMissingBean(TransactionRepository.class)
	@Bean
	public TransactionRepository jdbcTransactionRepository(JdbcTransactionRepository.DataSourceForSaga dataSourceForSaga,
	                                                       ObjectSerializer objectSerializer) {
		return new JdbcTransactionRepository(dataSourceForSaga, objectSerializer);
	}

	@Configuration
	@ConditionalOnClass(DruidDataSource.class)
	@ConditionalOnProperty(prefix = "spring.saga.repository.jdbc", name = "url")
	@ConditionalOnMissingBean(JdbcTransactionRepository.DataSourceForSaga.class)
	public static class DruidDataSourceAutoConfiguration {
		@Bean
		public JdbcTransactionRepository.DataSourceForSaga dataSourceForSaga(SagaProperties sagaProperties) {
			DruidDataSource dataSource = new DruidDataSource();

			SagaProperties.JdbcRepositoryProperties jdbcProperties = sagaProperties.getRepository().getJdbc();

			dataSource.setUrl(jdbcProperties.getUrl());
			dataSource.setDriverClassName(jdbcProperties.getDriverName());
			dataSource.setUsername(jdbcProperties.getUserName());
			dataSource.setPassword(jdbcProperties.getPassword());

			if (jdbcProperties.getMaxActive() > 0) {
				dataSource.setMaxActive(jdbcProperties.getMaxActive());
			}

			return new JdbcTransactionRepository.DataSourceForSaga(dataSource);
		}
	}
}
