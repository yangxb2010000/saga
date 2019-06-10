package com.tim.saga.demo.springcloud.inventory.service.impl;

import com.netflix.discovery.converters.Auto;
import com.tim.saga.demo.springcloud.inventory.InventoryServiceApplicationTest;
import com.tim.saga.demo.springcloud.inventory.dto.InventoryDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class InventoryServiceImplTest extends InventoryServiceApplicationTest {

	@Autowired
	private InventoryServiceImpl inventoryService;

	@Test
	public void decrease() {
		InventoryDTO inventoryDTO = new InventoryDTO();
		inventoryDTO.setOrderId(123L);
		inventoryDTO.setProductId(10000L);
		inventoryDTO.setCount(5);

		inventoryService.decrease(inventoryDTO);
	}

	@Test
	public void cancelDecrease() {
		InventoryDTO inventoryDTO = new InventoryDTO();

		inventoryDTO.setOrderId(123L);
		inventoryDTO.setProductId(10000L);
		inventoryDTO.setCount(5);

		inventoryService.cancelDecrease(inventoryDTO);
	}

	@Test
	public void findByProductId() {
	}
}