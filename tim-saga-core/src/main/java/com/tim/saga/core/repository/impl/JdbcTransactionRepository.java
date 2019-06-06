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
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

/**
 * @author xiaobing
 */
public class JdbcTransactionRepository implements TransactionRepository {
	private JdbcTemplate jdbcTemplate;
	private ObjectSerializer objectSerializer;

	public JdbcTransactionRepository(DataSourceForSaga dataSourceForSaga,
	                                 ObjectSerializer objectSerializer) {
		Assert.notNull(dataSourceForSaga, "dataSourceForSaga can not be null");
		Assert.notNull(objectSerializer, "objectSerializer can not be null");

		this.jdbcTemplate = new JdbcTemplate(dataSourceForSaga.getDataSource());
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
		return this.jdbcTemplate.update("insert into `t_saga_transaction` (`id`, `name`,`create_time`, `last_update_time`, `status`, `application_id`, `retried_count`) values (?,?,?,?,?,?,?)",
				transaction.getId(),
				transaction.getName(),
				transaction.getCreateTime(),
				transaction.getLastUpdateTime(),
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
		return this.jdbcTemplate.update("update t_saga_transaction set `last_update_time` = ?, `status` = ? where `id` = ?",
				System.currentTimeMillis(),
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
		return this.jdbcTemplate.update("update `t_saga_transaction` set `last_update_time` = ?, `status` = ?, `retried_count` = ? where `id` = ?",
				System.currentTimeMillis(),
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
		return this.jdbcTemplate.update("insert into `t_saga_participant` (`id`, `transaction_id`, `name`, `create_time`, `last_update_time`, `status`, `cancel_invocation_context`) values (?,?,?,?,?,?,?)",
				participant.getId(),
				participant.getTransactionId(),
				participant.getName(),
				participant.getCreateTime(),
				participant.getLastUpdateTime(),
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
		return this.jdbcTemplate.update("update t_saga_participant set `last_update_time` = ?, `status` = ? where `id` = ?",
				System.currentTimeMillis(),
				participant.getStatus().getValue(),
				participant.getId());
	}

	/**
	 * 获取最后更新时间 < date 的未提交的事务
	 *
	 * @param date
	 * @param status
	 * @param maxRetriedCount
	 * @return
	 */
	@Override
	public List<SagaTransaction> findUnmodifiedSince(String applicationId, Date date, EnumTransactionStatus status, int maxRetriedCount, int limit) {
		return this.jdbcTemplate.query("select `id`, `name`,`create_time`, `last_update_time`, `status`, `application_id`, `retried_count` from t_saga_transaction " +
						"where `application_id` = ? and `last_update_time` < ? and `status` = ? and `retried_count` < ? limit ?",
				new Object[]{
						applicationId,
						date.getTime(),
						status.getValue(),
						maxRetriedCount,
						limit
				}, (resultSet, i) -> {
					SagaTransaction transaction = new SagaTransaction();
					transaction.setId(resultSet.getString("id"));
					transaction.setName(resultSet.getString("name"));
					transaction.setCreateTime(resultSet.getLong("create_time"));
					transaction.setLastUpdateTime(resultSet.getLong("last_update_time"));
					transaction.setStatus(EnumTransactionStatus.indexOf(resultSet.getInt("status")));
					transaction.setApplicationId(resultSet.getString("application_id"));
					transaction.setRetriedCount(resultSet.getInt("retried_count"));
					return transaction;
				});
	}

	/**
	 * 获取该事务所有未取消的参与方
	 *
	 * @param transactionId
	 * @return
	 */
	@Override
	public List<SagaParticipant> getAllUnCanceledParticipant(String transactionId) {
		return this.jdbcTemplate.query("select `id`, `transaction_id`, `name`, `create_time`, `last_update_time`, `status`, `cancel_invocation_context` from t_saga_participant" +
						" where `transaction_id` = ? and `status` = ? ",
				new Object[]{
						transactionId,
						EnumParticipantStatus.New.getValue()
				}, (resultSet, i) -> {
					SagaParticipant participant = new SagaParticipant();
					participant.setId(resultSet.getString("id"));
					participant.setTransactionId(resultSet.getString("transaction_id"));
					participant.setName(resultSet.getString("name"));
					participant.setCreateTime(resultSet.getLong("create_time"));
					participant.setLastUpdateTime(resultSet.getLong("last_update_time"));
					participant.setStatus(EnumParticipantStatus.indexOf(resultSet.getInt("status")));

					InvocationContext cancelInvocationContext = this.objectSerializer.deSerialize(resultSet.getBytes("cancel_invocation_context"), InvocationContext.class);
					participant.setCancelInvocationContext(cancelInvocationContext);

					return participant;
				});
	}
}
