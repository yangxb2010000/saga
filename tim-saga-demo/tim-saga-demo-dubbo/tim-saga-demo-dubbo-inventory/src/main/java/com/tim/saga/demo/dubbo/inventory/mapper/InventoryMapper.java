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

package com.tim.saga.demo.dubbo.inventory.mapper;

import com.tim.saga.demo.springcloud.inventory.dto.InventoryDTO;
import com.tim.saga.demo.springcloud.inventory.entity.InventoryDO;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


/**
 * @author xiaobing
 */
public interface InventoryMapper {
	/**
	 * Decrease int.
	 *
	 * @param inventoryDTO the inventory dto
	 * @return the int
	 */
	@Update("update inventory set inventory_count = inventory_count - #{count}, update_time = now()" +
			" where product_id = #{productId} and inventory_count > #{count}")
	int decrease(InventoryDTO inventoryDTO);

	/**
	 * Decrease int.
	 *
	 * @param inventoryDTO the inventory dto
	 * @return the int
	 */
	@Update("update inventory set inventory_count = inventory_count + #{count}, update_time = now()" +
			" where product_id = #{productId}")
	int add(InventoryDTO inventoryDTO);

	/**
	 * Find by product id inventory do.
	 *
	 * @param productId the product id
	 * @return the inventoryCount do
	 */
	@Select("select id,product_id, inventory_count from inventory where product_id =#{productId}")
	InventoryDO findByProductId(String productId);

}
