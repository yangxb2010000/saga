package com.tim.saga.core.repository;

import com.tim.saga.core.transaction.EnumTransactionStatus;
import com.tim.saga.core.transaction.SagaParticipant;
import com.tim.saga.core.transaction.SagaTransaction;

import java.util.Date;
import java.util.List;


/**
 * @author xiaobing
 * <p>
 * 事务持久化接口，用于持久化saga事务
 */
public interface TransactionRepository {
	/**
	 * 创建
	 *
	 * @param transaction
	 * @return
	 */
	int create(SagaTransaction transaction);

	/**
	 * 更新事务状态
	 *
	 * @param transaction
	 * @return
	 */
	int updateTransactionStatus(SagaTransaction transaction);

	/**
	 * 更新Transaction
	 *
	 * @param transaction
	 * @return
	 */
	int updateTransaction(SagaTransaction transaction);

	/**
	 * 添加Participant
	 *
	 * @param participant
	 * @return
	 */
	int addParticipant(SagaParticipant participant);

	/**
	 * 更新Participant状态
	 *
	 * @param participant
	 * @return
	 */
	int updateParticipantStatus(SagaParticipant participant);

	/**
	 * 获取最后更新时间 < date 的未提交的事务
	 *
	 * @param applicationId   限制事务的applicationId，只有当前应用可以处理的事务才需要返回
	 * @param date            限制事务的最后一次修改时间， < date的才需要处理
	 * @param status          限制事务的状态
	 * @param maxRetriedCount 限制事务的重试次数，retriedCount > maxRetriedCount的就不在重试
	 * @param limit           限制一次性获取的事务条数
	 * @return
	 */
	List<SagaTransaction> findUnmodifiedSince(String applicationId, Date date, EnumTransactionStatus status, int maxRetriedCount, int limit);

	/**
	 * 获取该事务所有未取消的参与方
	 *
	 * @param transactionId
	 * @return
	 */
	List<SagaParticipant> getAllUnCanceledParticipant(String transactionId);

}
