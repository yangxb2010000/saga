package com.tim.saga.core.interceptor;

import com.tim.saga.core.InvocationContext;
import com.tim.saga.core.SagaApplicationContext;
import com.tim.saga.core.SagaTransactionManager;
import com.tim.saga.core.annotation.SagaParticipative;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author xiaobing
 */
@Aspect
public class SagaParticipativeAspect {
	private static Logger logger = LoggerFactory.getLogger(SagaParticipativeAspect.class);

	private SagaApplicationContext sagaApplicationContext;

	public SagaParticipativeAspect(SagaApplicationContext sagaApplicationContext) {
		this.sagaApplicationContext = sagaApplicationContext;
	}

	@Pointcut("@annotation(com.tim.saga.core.annotation.SagaParticipative)")
	public void sagaParticipativePointcut() {
	}

	@Around("sagaParticipativePointcut()")
	public void around(ProceedingJoinPoint point) throws Throwable {
		MethodSignature signature = (MethodSignature) point.getSignature();

		logger.debug("transaction begin for class: {}, method: {}", signature.getDeclaringType(), signature.getName());

		SagaParticipative sagaParticipative = signature.getMethod().getAnnotation(SagaParticipative.class);

		String participantName = sagaParticipative.name();
		if (StringUtils.isEmpty(participantName)) {
			participantName = signature.getDeclaringTypeName() + ":" + signature.getMethod().getName();
		}

		Assert.notNull(sagaParticipative.cancelMethod(), "Cancel method for SagaParticipative cannot be null");

		InvocationContext cancelInvocationContext = new InvocationContext(signature.getDeclaringType(), sagaParticipative.cancelMethod(), signature.getParameterTypes(), point.getArgs());

		sagaApplicationContext.getTransactionManager().enlistParticipant(participantName, cancelInvocationContext);

		logger.debug("enlist Participant class: {}, method: {} to transaction", signature.getDeclaringTypeName(), signature.getMethod().getName());

		point.proceed();
	}
}
