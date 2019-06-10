package com.tim.saga.demo.springcloud.inventory.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author xiaobing
 */
@Data
public class InventoryHistoryDO {
	private Long id;

	private Date createTime;

	private Date updateTime;

	private Long productId;

	private Long orderId;

	private Integer reduceCount;

	/**
	 * 标示该条记录是否已经被rollback过了
	 */
	private Boolean hasRollback;
}
