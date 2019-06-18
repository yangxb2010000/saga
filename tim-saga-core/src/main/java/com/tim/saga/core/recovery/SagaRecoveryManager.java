package com.tim.saga.core.recovery;

import com.tim.saga.core.SagaApplicationContext;
import com.tim.saga.core.SagaConfig;
import com.tim.saga.core.transaction.EnumTransactionStatus;
import com.tim.saga.core.transaction.SagaParticipant;
import com.tim.saga.core.transaction.SagaTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author xiaobing
 * <p>
 * 事务恢复任务，定时检测未正常cancel的事务，执行cancel操作，保证事务的一致性
 */
public class SagaRecoveryManager {
    private SagaApplicationContext sagaApplicationContext;
    /**
     * 事务回滚失败的报警
     */
    private FailRecoverAlert failRecoverAlert;

    private static final Logger logger = LoggerFactory.getLogger(SagaRecoveryManager.class);

    public SagaRecoveryManager(SagaApplicationContext sagaApplicationContext) {
        this.sagaApplicationContext = sagaApplicationContext;
    }

    public void setFailRecoverAlert(FailRecoverAlert failRecoverAlert) {
        this.failRecoverAlert = failRecoverAlert;
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
        if (CollectionUtils.isEmpty(transactions)) {
            return;
        }

        for (SagaTransaction transaction : transactions) {
            try {
                List<SagaParticipant> participantList = sagaApplicationContext.getTransactionRepository().getAllUnCanceledParticipant(transaction.getId());
                transaction.setParticipantList(participantList);

                //正常回滚逻辑就会持久化更新transaction
                this.sagaApplicationContext.getTransactionManager().rollback(transaction);
            } catch (Exception ex) {
                logger.error("recover failed for transaction: {}, error: {}", transaction.getId(), ex);

                if (transaction.getRetriedCount() >= sagaApplicationContext.getSagaConfig().getRecoverConfig().getMaxRetryCount()) {
                    logger.error("recover failed with max retry count, will not try again. txid: {}, status: {}, retried count: {}, last error: {}", transaction.getId(), transaction.getStatus(), transaction.getRetriedCount(), ex);
                    this.alertCompleteFail(transaction, ex);
                }

                //如果有异常，需要都懂更新事务的状态和重试次数等
                this.sagaApplicationContext.getTransactionRepository().updateTransaction(transaction);
            }
        }
    }

    private void alertCompleteFail(SagaTransaction transaction, Exception ex) {
        if (this.failRecoverAlert != null) {
            try {
                failRecoverAlert.alertCompleteFail(transaction, ex.toString(), sagaApplicationContext.getSagaConfig().getRecoverConfig().getMaxRetryCount());
            } catch (Exception alertEx) {
                logger.error("failed to call completeFailRecover for transaction {}, alert class type: {}, ex: {}", transaction.getId(), failRecoverAlert.getClass().getName(), alertEx);
            }

        }
    }


}
