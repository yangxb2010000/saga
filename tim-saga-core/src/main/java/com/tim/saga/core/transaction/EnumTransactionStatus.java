package com.tim.saga.core.transaction;

/**
 * 事务状态
 */
public enum EnumTransactionStatus {
	New(1),

	Commit(2),

	Rollback(3);

	private int value;

	EnumTransactionStatus(int value) {
		this.value = value;
	}

	public int getValue(){
		return value;
	}

	public static EnumTransactionStatus indexOf(int value) {
		for (EnumTransactionStatus status : EnumTransactionStatus.values()) {
			if (status.getValue() == value) {
				return status;
			}
		}

		return null;
	}
}
