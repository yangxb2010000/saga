package com.tim.saga.demo.dubbo.account.mapper;

import com.tim.saga.demo.dubbo.account.entity.AccountHistoryDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * @author xiaobing
 * <p>
 * 账户基于订单变动的记录
 */
public interface AccountHistoryMapper {
	/**
	 * 添加一条账户变更的记录
	 *
	 * @param userId
	 * @param orderId
	 * @param reduceAmount
	 * @return
	 */
	@Insert("insert into account_history(create_time, update_time, order_id, user_id, reduce_amount) values (now(), now(), #{orderId}, #{userId}, #{reduceAmount})")
	int insert(@Param("userId") Long userId, @Param("orderId") Long orderId, @Param("reduceAmount") BigDecimal reduceAmount);

	/**
	 * 更新该变更记录为已回滚
	 *
	 * @param userId
	 * @param orderId
	 * @return
	 */
	@Insert("update account_history set has_rollback = 1, update_time = now() where order_id = #{orderId} and user_id = #{userId} and has_rollback != 1")
	int updateHasRollbacked(@Param("userId") Long userId, @Param("orderId") Long orderId);

	/**
	 * @param orderId
	 * @return
	 */
	@Select("select id, order_id, reduce_amount, has_rollback from account_history where order_id = #{orderId}")
	AccountHistoryDO findByOrderId(Integer orderId);
}
