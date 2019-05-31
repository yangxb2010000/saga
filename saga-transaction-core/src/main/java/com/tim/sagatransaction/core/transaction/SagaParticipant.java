package com.tim.sagatransaction.core.transaction;

import com.tim.sagatransaction.core.support.BeanFactoryBuilder;
import com.tim.sagatransaction.core.InvocationContext;
import com.tim.sagatransaction.core.exception.SagaExceptionStatusCode;
import com.tim.sagatransaction.core.exception.SagaTransactionException;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author xiaobing
 */
public class SagaParticipant implements Serializable {
	private static final long serialVersionUID = -950322387106037019L;

	private String id;
	private String transactionId;
	private InvocationContext cancelInvocationContext;
	private EnumParticipantStatus status;

	public SagaParticipant() {
	}

	public SagaParticipant(String transactionId, InvocationContext cancelInvocationContext) {
		Assert.notNull(cancelInvocationContext, "cancelInvocationContext should be null");
		Assert.notNull(transactionId, "transactionId should be null");

		this.id = UUID.randomUUID().toString();
		this.transactionId = transactionId;
		this.cancelInvocationContext = cancelInvocationContext;
		this.status = EnumParticipantStatus.New;
	}

	/**
	 * 调用该参与方的cancel方法
	 */
	public void cancel() {
		if (cancelInvocationContext.getMethodName() == null || cancelInvocationContext.getMethodName() == "") {
			return;
		}

		try {
			Object target = BeanFactoryBuilder.getBeanFactory().getBean(cancelInvocationContext.getTargetClass());

			Method method = target.getClass().getMethod(cancelInvocationContext.getMethodName(), cancelInvocationContext.getParameterTypes());

			method.invoke(target, cancelInvocationContext.getArgs());

		} catch (Exception e) {
			throw new SagaTransactionException(SagaExceptionStatusCode.FailInvokeCancelParticipant, e);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public InvocationContext getCancelInvocationContext() {
		return cancelInvocationContext;
	}

	public void setCancelInvocationContext(InvocationContext cancelInvocationContext) {
		this.cancelInvocationContext = cancelInvocationContext;
	}

	public EnumParticipantStatus getStatus() {
		return status;
	}

	public void setStatus(EnumParticipantStatus status) {
		this.status = status;
	}
}
