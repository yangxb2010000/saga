package com.tim.saga.core.transaction;

/**
 * 事务参与方状态
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

	public static EnumParticipantStatus indexOf(int value) {
		for (EnumParticipantStatus status : EnumParticipantStatus.values()) {
			if (status.getValue() == value) {
				return status;
			}
		}

		return null;
	}

}
