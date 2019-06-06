package com.tim.saga.core;

import java.io.Serializable;

/**
 * @author xiaobing
 */
public class InvocationContext implements Serializable {
	private static final long serialVersionUID = -8503200848355328831L;
	
	private Class targetClass;

	private String methodName;

	private Class[] parameterTypes;

	private Object[] args;

	public InvocationContext() {

	}

	public InvocationContext(Class targetClass, String methodName, Class[] parameterTypes, Object... args) {
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.targetClass = targetClass;
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

	public Class getTargetClass() {
		return targetClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public Class[] getParameterTypes() {
		return parameterTypes;
	}

}
