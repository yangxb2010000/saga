package com.tim.saga.core;

import com.tim.saga.core.repository.TransactionRepository;
import com.tim.saga.core.support.BeanFactory;
import com.tim.saga.core.transaction.EnumParticipantStatus;
import com.tim.saga.core.transaction.EnumTransactionStatus;
import com.tim.saga.core.transaction.SagaParticipant;
import com.tim.saga.core.transaction.SagaTransaction;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaobing
 */
public class SagaTransactionManager {
	private SagaConfig transactionConfig;
	private TransactionRepository transactionRepository;
	private ExecutorService executorService;
	private BeanFactory beanFactory;

	private static ThreadLocal<SagaTransactionContext> SagaTransactionContextHolder = new ThreadLocal<>();

	public SagaTransactionManager(SagaConfig transactionConfig,
	                              TransactionRepository transactionRepository,
	                              ExecutorService executorService,
	                              BeanFactory beanFactory) {
		this.transactionConfig = transactionConfig;
		this.transactionRepository = transactionRepository;
		this.executorService = executorService;
		this.beanFactory = beanFactory;

		if (this.transactionConfig.isAsyncCancel()
				|| this.transactionConfig.isConcurrentCancel()) {
			Assert.notNull(this.executorService, "executorService cannot be null when asyncCancel or concurrentCancels is true");
		}
	}

	public void begin(String name) {
		SagaTransactionContext context = SagaTransactionContextHolder.get();
		if (context == null) {
			SagaTransaction transaction = new SagaTransaction(transactionConfig.getApplicationId(), name);
			this.transactionRepository.create(transaction);

			context = SagaTransactionContext.builder().transaction(transaction).build();

			SagaTransactionContextHolder.set(context);
		}

		context.addLayerCount();
	}

	/**
	 * 释放事务
	 */
	public void release() {
		SagaTransactionContext context = SagaTransactionContextHolder.get();
		if (context == null) {
			return;
		}

		context.reduceLayerCount();

		// 如果嵌套层级已经为空，就释放SagaTransactionContextHolder资源
		if (context.getLayerCount() <= 0) {
			SagaTransactionContextHolder.remove();
		}
	}

	/**
	 * 回滚事务
	 */
	public void rollback() {
		//如果是内层的事务就直接返回
		if (!isLastTransaction()) {
			return;
		}

		SagaTransactionContext context = SagaTransactionContextHolder.get();

		if (this.transactionConfig.isAsyncCancel()) {
			this.executorService.submit(() -> {
				this.rollback(context.getTransaction());
			});
		} else {
			this.rollback(context.getTransaction());
		}
	}

	/**
	 * 执行rollback操作
	 *
	 * @param transaction
	 */
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
		//如果是内层的事务就直接返回
		if (!isLastTransaction()) {
			return;
		}

		SagaTransaction transaction = SagaTransactionContextHolder.get().getTransaction();

		transaction.setStatus(EnumTransactionStatus.Commit);

		this.transactionRepository.updateTransactionStatus(transaction);
	}

	/**
	 * 添加事务参与方
	 *
	 * @param invocationContext
	 */
	public void enlistParticipant(String name, InvocationContext invocationContext) {
		SagaTransactionContext context = SagaTransactionContextHolder.get();

		if (context == null || context.getTransaction() == null) {
			return;
		}

		SagaParticipant participant = new SagaParticipant(context.getTransaction().getId(), name, invocationContext);
		context.getTransaction().addParticipant(participant);

		this.transactionRepository.addParticipant(participant);
	}

	/**
	 * 判断当前事务是否是最后的
	 *
	 * @return
	 */
	private boolean isLastTransaction() {
		return SagaTransactionContextHolder.get() != null
				&& SagaTransactionContextHolder.get().getLayerCount() == 1;
	}

	/**
	 * 调用参与方的cancel操作
	 *
	 * @param participant
	 */
	private void cancelParticipant(SagaParticipant participant) {
		participant.cancel(this.beanFactory);
		participant.setStatus(EnumParticipantStatus.Canceled);
		this.transactionRepository.updateParticipantStatus(participant);
	}

	/**
	 * 调用参与方的cancel操作成功之后 更新Transaction状态
	 *
	 * @param transaction
	 */
	private void successRollbackTransaction(SagaTransaction transaction) {
		transaction.setStatus(EnumTransactionStatus.Rollback);
		this.transactionRepository.updateTransaction(transaction);
	}

	@Data
	@Builder
	public static class SagaTransactionContext {
		/**
		 * SagaTransaction
		 */
		private SagaTransaction transaction;

		/**
		 * 事务嵌套的层级数
		 */
		private int layerCount;

		/**
		 * 进入事务
		 */
		public void addLayerCount() {
			layerCount++;
		}

		/**
		 * 退出一个事务
		 */
		public void reduceLayerCount() {
			layerCount--;
		}
	}
}
