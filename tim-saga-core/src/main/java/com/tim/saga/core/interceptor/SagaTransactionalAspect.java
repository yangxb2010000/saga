package com.tim.saga.core.interceptor;

import com.tim.saga.core.SagaApplicationContext;
import com.tim.saga.core.annotation.SagaTransactional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author xiaobing
 * <p>
 * 处理定义事务的边界的切面
 */
@Aspect
public class SagaTransactionalAspect {
	private static Logger logger = LoggerFactory.getLogger(SagaTransactionalAspect.class);
	private SagaApplicationContext sagaApplicationContext;

	public SagaTransactionalAspect(SagaApplicationContext sagaApplicationContext) {
		this.sagaApplicationContext = sagaApplicationContext;
	}

	@Pointcut("@annotation(com.tim.saga.core.annotation.SagaTransactional)")
	public void sagaTransactionalPointcut() {
	}

	@Around("sagaTransactionalPointcut()")
	public void around(ProceedingJoinPoint point) throws Throwable {
		MethodSignature signature = (MethodSignature) point.getSignature();

		logger.debug("transaction begin for class: {}, method: {}", signature.getDeclaringType(), signature.getName());

		SagaTransactional sagaTransactional = signature.getMethod().getAnnotation(SagaTransactional.class);

		String transactionName = sagaTransactional.name();
		if (StringUtils.isEmpty(transactionName)) {
			transactionName = signature.getDeclaringTypeName() + ":" + signature.getMethod().getName();
		}

		logger.debug("transaction begin for class: {}, method: {}", signature.getDeclaringType(), signature.getName());

		try {
			sagaApplicationContext.getTransactionManager().begin(transactionName);

			point.proceed();

			sagaApplicationContext.getTransactionManager().commit();

			logger.debug("transaction rollback for class: {}, method: {}", signature.getDeclaringType(), signature.getName());

		} catch (Exception ex) {
			sagaApplicationContext.getTransactionManager().rollback();
			logger.debug("transaction rollback for class: {}, method: {}", signature.getDeclaringType(), signature.getName());
			throw ex;
		} finally {
			sagaApplicationContext.getTransactionManager().release();

			logger.debug("transaction release for class: {}, method: {}", signature.getDeclaringType(), signature.getName());
		}
	}
}
