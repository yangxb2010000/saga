package com.tim.sagatransaction.core.exception;

public enum SagaExceptionStatusCode {

	NotSupportedNestedTransaction(1),
	FailInvokeCancelParticipant(2);

	private int value;

	SagaExceptionStatusCode(int value) {
		this.value = value;
	}
}
