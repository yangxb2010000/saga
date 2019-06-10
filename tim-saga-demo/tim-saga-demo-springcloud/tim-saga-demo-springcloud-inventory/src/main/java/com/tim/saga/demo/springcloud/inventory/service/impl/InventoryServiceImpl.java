/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tim.saga.demo.springcloud.inventory.service.impl;

import com.tim.saga.core.exception.SagaException;
import com.tim.saga.demo.springcloud.inventory.dto.InventoryDTO;
import com.tim.saga.demo.springcloud.inventory.entity.InventoryDO;
import com.tim.saga.demo.springcloud.inventory.mapper.InventoryHistoryMapper;
import com.tim.saga.demo.springcloud.inventory.mapper.InventoryMapper;
import com.tim.saga.demo.springcloud.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author xiaobing
 */
@Service("inventoryService")
public class InventoryServiceImpl implements InventoryService {

	/**
	 * logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryServiceImpl.class);

	@Autowired
	private InventoryMapper inventoryMapper;

	@Autowired
	private InventoryHistoryMapper inventoryHistoryMapper;

	public InventoryServiceImpl() {

	}

	/**
	 * 扣减库存
	 *
	 * @param inventoryDTO 库存DTO对象
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean decrease(InventoryDTO inventoryDTO) {
		LOGGER.info("==========springcloud调用扣减库存decrease===========");
		int res = inventoryMapper.decrease(inventoryDTO);
		if (res <= 0) {
			throw new RuntimeException("扣减Inventory库存失败");
		}

		res = inventoryHistoryMapper.insert(inventoryDTO.getOrderId(), inventoryDTO.getProductId(), inventoryDTO.getCount());
		if (res <= 0) {
			throw new RuntimeException("添加InventoryHistory记录失败");
		}

		return true;
	}

	/**
	 * 扣减库存操作.
	 *
	 * @param inventoryDTO 库存DTO对象
	 * @return true
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean cancelDecrease(InventoryDTO inventoryDTO) {
		LOGGER.debug("============springcloud执行扣减库存的cancel操作===============");

		int res = inventoryHistoryMapper.updateHasRollbacked(inventoryDTO.getOrderId(), inventoryDTO.getProductId());
		if (res <= 0) {
			// 更新历史记录失败，说明已经回滚或者不存在，直接返回成功
			return true;
		}

		res = inventoryMapper.add(inventoryDTO);
		if (res <= 0) {
			throw new RuntimeException("添加Inventory库存失败");
		}

		return true;
	}

	/**
	 * 获取商品库存信息.
	 *
	 * @param productId 商品id
	 * @return InventoryDO
	 */
	@Override
	public InventoryDO findByProductId(String productId) {
		return inventoryMapper.findByProductId(productId);
	}

	@Override
	public Boolean decreaseWithException(InventoryDTO inventoryDTO) {
		//这里是模拟异常所以就直接抛出异常了
		throw new SagaException("库存扣减异常！");
	}

	@Override
	public Boolean decreaseWithTimeout(InventoryDTO inventoryDTO) {
		try {
			//模拟延迟 当前线程暂停10秒
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		LOGGER.info("==========springcloud调用扣减库存decreaseWithTimeout===========");

		final int decrease = inventoryMapper.decrease(inventoryDTO);
		if (decrease != 1) {
			throw new SagaException("库存不足");
		}

		return true;
	}
}
