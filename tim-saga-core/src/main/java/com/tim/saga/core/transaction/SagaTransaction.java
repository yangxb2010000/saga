package com.tim.saga.core.transaction;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author xiaobing
 */
public class SagaTransaction implements Serializable {
	private static final long serialVersionUID = 8457659281204985797L;

	/**
	 * 事务的唯一Id
	 */
	private String id;
	/**
	 * 事务状态
	 */
	private EnumTransactionStatus status;
	/**
	 * 事务方法名称
	 */
	private String name;
	/**
	 * createTime
	 */
	private long createTime = System.currentTimeMillis();
	/**
	 * lastUpdateTime
	 */
	private long lastUpdateTime = System.currentTimeMillis();
	/**
	 * 应用的唯一Id，用来区分事务的从属，防止事务会滚时混乱
	 */
	private String applicationId;

	/**
	 * 已经重试的次数
	 */
	private int retriedCount;

	/**
	 * 事务的参与方
	 */
	private transient List<SagaParticipant> participantList;

	public SagaTransaction() {

	}

	public SagaTransaction(String applicationId, String name) {
		this.id = UUID.randomUUID().toString();
		this.applicationId = applicationId;
		this.name = name;
		this.status = EnumTransactionStatus.New;
	}

	public void addParticipant(SagaParticipant... participantList) {
		if (this.participantList == null) {
			this.participantList = new ArrayList<>();
		}

		for (SagaParticipant participant : participantList) {
			this.participantList.add(participant);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public EnumTransactionStatus getStatus() {
		return status;
	}

	public void setStatus(EnumTransactionStatus status) {
		this.status = status;
	}

	public List<SagaParticipant> getParticipantList() {
		return participantList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRetriedCount() {
		return retriedCount;
	}

	public void setRetriedCount(int retriedCount) {
		this.retriedCount = retriedCount;
	}

	public void setParticipantList(List<SagaParticipant> participantList) {
		this.participantList = participantList;
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
}
