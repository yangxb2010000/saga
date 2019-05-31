package com.tim.sagatransaction.core.transaction;

/**
 * 事务状态
 */
public enum EnumParticipantStatus {
	New(1),

	Canceled(2);

	private int value;

	EnumParticipantStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
