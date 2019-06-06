package com.tim.saga.core.recovery;

import com.tim.saga.core.SagaApplicationContext;
import com.tim.saga.core.SagaConfig;
import com.tim.saga.core.transaction.EnumTransactionStatus;
import com.tim.saga.core.transaction.SagaParticipant;
import com.tim.saga.core.transaction.SagaTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author xiaobing
 */
public class SagaRecoveryManager {
	private SagaApplicationContext sagaApplicationContext;

	private static final Logger logger = LoggerFactory.getLogger(SagaRecoveryManager.class);

	public SagaRecoveryManager(SagaApplicationContext sagaApplicationContext) {
		this.sagaApplicationContext = sagaApplicationContext;
	}

	public void startRecover() {
		List<SagaTransaction> transactions = loadErrorTransactions();

		recoverErrorTransactions(transactions);
	}

	private List<SagaTransaction> loadErrorTransactions() {
		long currentTimeInMillis = System.currentTimeMillis();

		SagaConfig.RecoverConfig recoverConfig = sagaApplicationContext.getSagaConfig().getRecoverConfig();

		Date since = new Date(currentTimeInMillis - recoverConfig.getRecoverDuration() * 1000);

		return sagaApplicationContext.getTransactionRepository().findUnmodifiedSince(
				sagaApplicationContext.getSagaConfig().getApplicationId(),
				since,
				EnumTransactionStatus.New,
				recoverConfig.getMaxRetryCount(),
				recoverConfig.getBatchHandleTransactionCount());
	}

	private void recoverErrorTransactions(List<SagaTransaction> transactions) {
		if (transactions == null) {
			return;
		}

		for (SagaTransaction transaction : transactions) {
			try {
				transaction.setRetriedCount(transaction.getRetriedCount() + 1);
				transaction.setLastUpdateTime(System.currentTimeMillis());

				List<SagaParticipant> participantList = sagaApplicationContext.getTransactionRepository().getAllUnCanceledParticipant(transaction.getId());
				transaction.setParticipantList(participantList);

				//正常回滚逻辑就会持久化更新transaction
				this.sagaApplicationContext.getTransactionManager().rollback(transaction);
			} catch (Throwable throwable) {
				if (transaction.getRetriedCount() > sagaApplicationContext.getSagaConfig().getRecoverConfig().getMaxRetryCount()) {
					logger.error(String.format("recover failed with max retry count, will not try again. txid: %s, status: %s, retried count: %d", transaction.getId(), transaction.getStatus(), transaction.getRetriedCount()));
				}

				//如果有异常，需要都懂更新事务的状态和重试次数等
				this.sagaApplicationContext.getTransactionRepository().updateTransaction(transaction);
			}
		}
	}
}
