package com.tim.sagatransaction.core;

import com.tim.sagatransaction.core.exception.SagaExceptionStatusCode;
import com.tim.sagatransaction.core.exception.SagaTransactionException;
import com.tim.sagatransaction.core.repository.TransactionRepository;
import com.tim.sagatransaction.core.transaction.EnumParticipantStatus;
import com.tim.sagatransaction.core.transaction.EnumTransactionStatus;
import com.tim.sagatransaction.core.transaction.SagaParticipant;
import com.tim.sagatransaction.core.transaction.SagaTransaction;
import org.springframework.util.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaobing
 */
public class TransactionManager {
	private SagaTransactionConfig transactionConfig;
	private TransactionRepository transactionRepository;
	private ExecutorService executorService;
	private static ThreadLocal<SagaTransaction> SagaTransactionHolder = new ThreadLocal<SagaTransaction>();

	public TransactionManager(SagaTransactionConfig transactionConfig,
	                          TransactionRepository transactionRepository,
	                          ExecutorService executorService) {
		this.transactionConfig = transactionConfig;
		this.transactionRepository = transactionRepository;
		this.executorService = executorService;

		if (this.transactionConfig.isAsyncCancel()
				|| this.transactionConfig.isConcurrentCancel()) {
			Assert.notNull(this.executorService, "executorService cannot be null when asyncCancel or concurrentCancels is true");
		}
	}

	public void begin(String name) {
		if (SagaTransactionHolder.get() != null) {
			throw new SagaTransactionException(SagaExceptionStatusCode.NotSupportedNestedTransaction);
		}
		SagaTransaction transaction = new SagaTransaction(transactionConfig.getApplicationId(), name);
		SagaTransactionHolder.set(transaction);
	}

	/**
	 * 回滚事务
	 */
	public void rollback() {
		checkTransaction();

		SagaTransaction transaction = SagaTransactionHolder.get();

		if (this.transactionConfig.isAsyncCancel()) {
			this.executorService.submit(() -> {
				this.rollback(transaction);
			});
		} else {
			this.rollback(transaction);
		}
	}

	public void rollback(SagaTransaction transaction) {
		if (transaction == null
				|| transaction.getParticipantList() == null
				|| transaction.getParticipantList().size() <= 0) {
			return;
		}

		if (this.transactionConfig.isConcurrentCancel()) {
			//并发执行cancel
			CountDownLatch countDownLatch = new CountDownLatch(transaction.getParticipantList().size());
			for (SagaParticipant participant : transaction.getParticipantList()) {
				this.executorService.submit(() -> {
					this.cancelParticipant(participant);
					countDownLatch.countDown();
				});
			}

			try {
				if (countDownLatch.await(this.transactionConfig.getWaitConcurrentCancelTimeoutInMs(), TimeUnit.MILLISECONDS)) {
					this.successRollbackTransaction(transaction);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			//同步执行cancel
			for (SagaParticipant participant : transaction.getParticipantList()) {
				this.cancelParticipant(participant);
			}

			this.successRollbackTransaction(transaction);
		}
	}

	/**
	 * 提交事务
	 */
	public void commit() {
		checkTransaction();
		SagaTransaction transaction = SagaTransactionHolder.get();
		transaction.setStatus(EnumTransactionStatus.Commit);
		this.transactionRepository.updateTransactionStatus(transaction);
	}

	/**
	 * 释放事务
	 */
	public void release() {
		SagaTransactionHolder.remove();
	}

	/**
	 * 添加事务参与方
	 *
	 * @param invocationContext
	 */
	public void enlistParticipant(InvocationContext invocationContext) {
		checkTransaction();
		SagaTransaction transaction = SagaTransactionHolder.get();

		SagaParticipant participant = new SagaParticipant(transaction.getId(), invocationContext);
		SagaTransactionHolder.get().addParticipant(participant);
		this.transactionRepository.addParticipant(participant);
	}

	private void checkTransaction() {
		SagaTransaction transaction = SagaTransactionHolder.get();
		Assert.notNull(transaction, "transaction is null, please check whether transaction is opened.");
	}

	private void cancelParticipant(SagaParticipant participant) {
		participant.cancel();
		participant.setStatus(EnumParticipantStatus.Canceled);
		this.transactionRepository.updateParticipantStatus(participant);
	}

	private void successRollbackTransaction(SagaTransaction transaction) {
		transaction.setStatus(EnumTransactionStatus.Rollback);
		this.transactionRepository.updateTransactionStatus(transaction);
	}
}
