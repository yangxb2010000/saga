package com.tim.saga.demo.dubbo.account.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xiaobing
 * <p>
 * Account的修改历史，用来实现payment cancel的幂等操作
 */
@Data
public class AccountHistoryDO implements Serializable {
	private static final long serialVersionUID = 2410240654342783928L;

	private Long id;

	private Date createTime;

	private Date updateTime;

	private Long userId;

	private String orderId;

	private BigDecimal reduceAmount;

	/**
	 * 标示该条记录是否已经被rollback过了
	 */
	private Boolean hasRollback;

}
