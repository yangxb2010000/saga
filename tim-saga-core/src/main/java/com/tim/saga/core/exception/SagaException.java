package com.tim.saga.core.exception;

/**
 * @author xiaobing
 */
public class SagaException extends RuntimeException {

	public SagaException(String message, Exception ex) {
		super(message, ex);
	}

	public SagaException(String message) {
		super(message);
	}
}
