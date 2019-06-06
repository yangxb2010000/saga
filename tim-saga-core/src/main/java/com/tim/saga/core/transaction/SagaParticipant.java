package com.tim.saga.core.transaction;

import com.tim.saga.core.InvocationContext;
import com.tim.saga.core.exception.SagaException;
import com.tim.saga.core.support.BeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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
	private String name;
	private long createTime = System.currentTimeMillis();
	private long lastUpdateTime = System.currentTimeMillis();
	private InvocationContext cancelInvocationContext;
	private EnumParticipantStatus status;

	public SagaParticipant() {
	}

	public SagaParticipant(String transactionId,
	                       String name,
	                       InvocationContext cancelInvocationContext) {
		Assert.notNull(cancelInvocationContext, "cancelInvocationContext should be null");
		Assert.notNull(transactionId, "transactionId should be null");

		this.id = UUID.randomUUID().toString();
		this.transactionId = transactionId;
		this.name = name;
		this.cancelInvocationContext = cancelInvocationContext;
		this.status = EnumParticipantStatus.New;
	}

	/**
	 * 调用该参与方的cancel方法
	 */
	public void cancel(BeanFactory beanFactory) {
		if (StringUtils.isEmpty(cancelInvocationContext.getMethodName())) {
			return;
		}

		Assert.notNull(beanFactory, "beanFactory can not be null");

		try {
			Object target = beanFactory.getBean(cancelInvocationContext.getTargetClass());

			Method method = target.getClass().getMethod(cancelInvocationContext.getMethodName(), cancelInvocationContext.getParameterTypes());

			method.invoke(target, cancelInvocationContext.getArgs());

		} catch (Exception e) {
			throw new SagaException("failed to invoke cancel method, err: " + e.getMessage(), e);
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

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
