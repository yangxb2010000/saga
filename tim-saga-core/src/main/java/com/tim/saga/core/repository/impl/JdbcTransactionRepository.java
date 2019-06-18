package com.tim.saga.core.repository.impl;

import com.tim.saga.core.InvocationContext;
import com.tim.saga.core.repository.TransactionRepository;
import com.tim.saga.core.serializer.ObjectSerializer;
import com.tim.saga.core.transaction.EnumParticipantStatus;
import com.tim.saga.core.transaction.EnumTransactionStatus;
import com.tim.saga.core.transaction.SagaParticipant;
import com.tim.saga.core.transaction.SagaTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xiaobing
 */
public class JdbcTransactionRepository implements TransactionRepository {
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private ObjectSerializer objectSerializer;

    public JdbcTransactionRepository(DataSourceForSaga dataSourceForSaga,
                                     ObjectSerializer objectSerializer) {
        Assert.notNull(dataSourceForSaga, "dataSourceForSaga can not be null");
        Assert.notNull(objectSerializer, "objectSerializer can not be null");

        this.jdbcTemplate = new JdbcTemplate(dataSourceForSaga.getDataSource());
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        this.objectSerializer = objectSerializer;
    }

    @Data
    @AllArgsConstructor
    /**
     * DataSource的包装类，方便区分用户自己的DataSource bean
     */
    public static class DataSourceForSaga {
        private DataSource dataSource;
    }

    /**
     * 创建
     *
     * @param transaction
     * @return
     */
    @Override
    public int create(SagaTransaction transaction) {
        return this.jdbcTemplate.update("insert into `t_saga_transaction` (`transaction_id`, `name`,`create_time`, `last_update_time`, `status`, `application_id`, `retried_count`) values (?,?,?,?,?,?,?)",
                transaction.getId(),
                transaction.getName(),
                new Date(transaction.getCreateTime()),
                new Date(transaction.getLastUpdateTime()),
                transaction.getStatus().getValue(),
                transaction.getApplicationId(),
                transaction.getRetriedCount());
    }

    /**
     * 更新事务状态
     *
     * @param transaction
     * @return
     */
    @Override
    public int updateTransactionStatus(SagaTransaction transaction) {
        return this.jdbcTemplate.update("update t_saga_transaction set `last_update_time` = ?, `status` = ? where `transaction_id` = ?",
                new Date(System.currentTimeMillis()),
                transaction.getStatus().getValue(),
                transaction.getId());
    }

    /**
     * 更新Transaction
     *
     * @param transaction
     * @return
     */
    @Override
    public int updateTransaction(SagaTransaction transaction) {
        return this.jdbcTemplate.update("update `t_saga_transaction` set `last_update_time` = ?, `status` = ?, `retried_count` = ? where `transaction_id` = ?",
                new Date(System.currentTimeMillis()),
                transaction.getStatus().getValue(),
                transaction.getRetriedCount(),
                transaction.getId());
    }

    /**
     * 添加Participant
     *
     * @param participant
     * @return
     */
    @Override
    public int addParticipant(SagaParticipant participant) {
        byte[] cancelInvocationContext = this.objectSerializer.serialize(participant.getCancelInvocationContext());
        return this.jdbcTemplate.update("insert into `t_saga_participant` (`participant_id`, `transaction_id`, `name`, `create_time`, `last_update_time`, `status`, `cancel_invocation_context`) values (?,?,?,?,?,?,?)",
                participant.getId(),
                participant.getTransactionId(),
                participant.getName(),
                new Date(participant.getCreateTime()),
                new Date(participant.getLastUpdateTime()),
                participant.getStatus().getValue(),
                cancelInvocationContext);
    }

    /**
     * 更新Participant状态
     *
     * @param participant
     * @return
     */
    @Override
    public int updateParticipantStatus(SagaParticipant participant) {
        return this.jdbcTemplate.update("update t_saga_participant set `last_update_time` = ?, `status` = ? where `participant_id` = ?",
                new Date(System.currentTimeMillis()),
                participant.getStatus().getValue(),
                participant.getId());
    }

    /**
     * 获取最后更新时间 < date 的未提交的事务
     *
     * @param date            最后更新时间
     * @param status          事务状态
     * @param maxRetriedCount 事务最大重试X次，大于X次的就不再返回
     * @param limit           一次获取X个事务
     * @return
     */
    @Override
    @Transactional
    public List<SagaTransaction> findUnmodifiedSince(String applicationId, Date date, EnumTransactionStatus status, int maxRetriedCount, int limit) {
        // 此处使用for update悲观锁，防止多实例获取到同一条需要恢复的数据
        List<SagaTransaction> transactionList = this.jdbcTemplate.query("select `transaction_id`, `name`,`create_time`, `last_update_time`, `status`, `application_id`, `retried_count` from t_saga_transaction " +
                        "where `application_id` = ? and `last_update_time` < ? and `status` = ? and `retried_count` < ? limit ? for update",
                new Object[]{
                        applicationId,
                        date,
                        status.getValue(),
                        maxRetriedCount,
                        limit
                }, (resultSet, i) -> {
                    SagaTransaction transaction = new SagaTransaction();
                    transaction.setId(resultSet.getString("transaction_id"));
                    transaction.setName(resultSet.getString("name"));
                    transaction.setCreateTime(resultSet.getDate("create_time").getTime());
                    transaction.setLastUpdateTime(resultSet.getDate("last_update_time").getTime());
                    transaction.setStatus(EnumTransactionStatus.indexOf(resultSet.getInt("status")));
                    transaction.setApplicationId(resultSet.getString("application_id"));
                    transaction.setRetriedCount(resultSet.getInt("retried_count"));
                    return transaction;
                });

        if (CollectionUtils.isEmpty(transactionList)) {
            return transactionList;
        }

        //更新last_update_time防止释放锁之后立即被其他Recover实例获取到
        List<String> transactionIdList = transactionList.stream().map(transaction -> transaction.getId()).collect(Collectors.toList());

        Map<String, Object> params = new HashMap(2);
        params.put("lastUpdateTime", new Date(System.currentTimeMillis()));
        params.put("transactionIdList", transactionIdList);

        this.namedParameterJdbcTemplate.update("update t_saga_transaction set last_update_time = :lastUpdateTime  where `transaction_id` in (:transactionIdList)", params);

        return transactionList;
    }

    /**
     * 获取该事务所有未取消的参与方
     *
     * @param transactionId
     * @return
     */
    @Override
    public List<SagaParticipant> getAllUnCanceledParticipant(String transactionId) {
        return this.jdbcTemplate.query("select `participant_id`, `transaction_id`, `name`, `create_time`, `last_update_time`, `status`, `cancel_invocation_context` from t_saga_participant" +
                        " where `transaction_id` = ? and `status` = ? ",
                new Object[]{
                        transactionId,
                        EnumParticipantStatus.New.getValue()
                }, (resultSet, i) -> {
                    SagaParticipant participant = new SagaParticipant();
                    participant.setId(resultSet.getString("participant_id"));
                    participant.setTransactionId(resultSet.getString("transaction_id"));
                    participant.setName(resultSet.getString("name"));
                    participant.setCreateTime(resultSet.getDate("create_time").getTime());
                    participant.setLastUpdateTime(resultSet.getDate("last_update_time").getTime());
                    participant.setStatus(EnumParticipantStatus.indexOf(resultSet.getInt("status")));

                    InvocationContext cancelInvocationContext = this.objectSerializer.deSerialize(resultSet.getBytes("cancel_invocation_context"), InvocationContext.class);
                    participant.setCancelInvocationContext(cancelInvocationContext);

                    return participant;
                });
    }
}
