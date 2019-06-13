package com.tim.saga.demo.dubbo.inventory.api;

import com.tim.saga.core.annotation.SagaParticipative;

public interface InventoryService {

	/**
	 * 库存扣减
	 *
	 * @param inventoryDTO 实体对象
	 * @return true 成功
	 */
	@SagaParticipative(cancelMethod = "cancelDecrease")
	Boolean decrease(InventoryDTO inventoryDTO);

	/**
	 * 库存扣减的cancel操作
	 *
	 * @param inventoryDTO 实体对象
	 * @return true 成功
	 */
	Boolean cancelDecrease(InventoryDTO inventoryDTO);

	/**
	 * 获取商品库存
	 *
	 * @param productId 商品id
	 * @return InventoryDO
	 */
	Integer findByProductId(String productId);


	/**
	 * 模拟库存扣减异常
	 *
	 * @param inventoryDTO 实体对象
	 * @return true 成功
	 */
	@SagaParticipative(cancelMethod = "cancelDecrease")
	Boolean decreaseWithException(InventoryDTO inventoryDTO);

	/**
	 * 模拟库存扣减超时
	 *
	 * @param inventoryDTO 实体对象
	 * @return true 成功
	 */
	@SagaParticipative(cancelMethod = "cancelDecrease")
	Boolean decreaseWithTimeout(InventoryDTO inventoryDTO);
}