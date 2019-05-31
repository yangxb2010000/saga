package com.tim.sagatransaction.core.exception;

import java.util.concurrent.ExecutorService;

/**
 * @author xiaobing
 */
public class SagaTransactionException extends RuntimeException {
	private SagaExceptionStatusCode statusCode;

	public SagaTransactionException(SagaExceptionStatusCode statusCode) {
		this.statusCode = statusCode;
	}

	public SagaTransactionException(SagaExceptionStatusCode statusCode, Exception ex) {
		super(ex);

		this.statusCode = statusCode;
	}

	public SagaExceptionStatusCode getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(SagaExceptionStatusCode statusCode) {
		this.statusCode = statusCode;
	}
}
