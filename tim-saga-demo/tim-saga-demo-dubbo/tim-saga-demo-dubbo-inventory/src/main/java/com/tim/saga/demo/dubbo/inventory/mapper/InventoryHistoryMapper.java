package com.tim.saga.demo.dubbo.inventory.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author xiaobing
 */
public interface InventoryHistoryMapper {
	/**
	 * 添加一条账户变更的记录
	 *
	 * @param orderId
	 * @param productId
	 * @param reduceCount
	 * @return
	 */
	@Insert("insert into inventory_history(create_time, update_time, order_id, product_id, reduce_count) values (now(), now(), #{orderId}, #{productId}, #{reduceCount})")
	int insert(@Param("orderId") Long orderId, @Param("productId") Long productId, @Param("reduceCount") Integer reduceCount);

	/**
	 * 更新该变更记录为已回滚
	 *
	 * @param orderId
	 * @param productId
	 * @return
	 */
	@Insert("update inventory_history set has_rollback = 1, update_time = now() where order_id = #{orderId} and product_id = #{productId} and has_rollback != 1")
	int updateHasRollbacked(@Param("orderId") Long orderId, @Param("productId") Long productId);
}
