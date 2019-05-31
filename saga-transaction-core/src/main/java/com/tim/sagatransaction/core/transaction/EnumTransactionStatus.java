package com.tim.sagatransaction.core.transaction;

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
}
