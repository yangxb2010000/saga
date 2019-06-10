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

package com.tim.saga.demo.springcloud.inventory.controller;

import com.tim.saga.demo.springcloud.inventory.dto.InventoryDTO;
import com.tim.saga.demo.springcloud.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author xiaobing
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

	private final InventoryService inventoryService;

	@Autowired
	public InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@RequestMapping("/decrease")
	public Boolean decrease(@RequestBody InventoryDTO inventoryDTO) {
		return inventoryService.decrease(inventoryDTO);
	}

	@RequestMapping("/decrease/cancel")
	public Boolean cancelDecrease(@RequestBody InventoryDTO inventoryDTO) {
		return inventoryService.cancelDecrease(inventoryDTO);
	}

	@RequestMapping("/findByProductId")
	public Integer findByProductId(@RequestParam("productId") String productId) {
		return inventoryService.findByProductId(productId).getInventoryCount();
	}

	@RequestMapping("/decreaseWithException")
	public Boolean decreaseWithTryException(@RequestBody InventoryDTO inventoryDTO) {
		return inventoryService.decreaseWithException(inventoryDTO);
	}

	@RequestMapping("/mockWithTimeout")
	public Boolean decreaseWithTimeout(@RequestBody InventoryDTO inventoryDTO) {
		return inventoryService.decreaseWithTimeout(inventoryDTO);
	}

}
