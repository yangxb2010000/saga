package com.tim.sagatransaction.core.transaction;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

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
	private Date createTime = new Date();
	private Date lastUpdateTime = new Date();
	/**
	 * 应用的唯一Id，用来区分事务的从属，防止事务会滚时混乱
	 */
	private String applicationId;

	private transient List<SagaParticipant> participantList;

	public SagaTransaction(String applicationId, String name) {
		this.id = UUID.randomUUID().toString();
		this.applicationId = applicationId;
		this.name = name;
	}

	public void addParticipant(SagaParticipant participant) {
		if (participantList == null) {
			participantList = new ArrayList<>();
		}

		participantList.add(participant);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
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

}
