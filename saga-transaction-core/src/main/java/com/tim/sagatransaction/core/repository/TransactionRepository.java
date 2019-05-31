package com.tim.sagatransaction.core.repository;

import com.tim.sagatransaction.core.transaction.EnumTransactionStatus;
import com.tim.sagatransaction.core.transaction.SagaParticipant;
import com.tim.sagatransaction.core.transaction.SagaTransaction;

import java.util.Date;
import java.util.List;

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


	SagaTransaction findByTransactionId(String transactionId);

	List<SagaTransaction> findAllUnmodifiedSince(Date date);

	List<SagaParticipant> getAllParticipant(String transactionId);

}
