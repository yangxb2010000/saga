package com.tim.saga.demo.springcloud.account.service.impl;

import com.netflix.discovery.converters.Auto;
import com.tim.saga.demo.springcloud.account.AccountServiceApplicationTest;
import com.tim.saga.demo.springcloud.account.dto.AccountDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class AccountServiceImplTest extends AccountServiceApplicationTest {

	@Autowired
	AccountServiceImpl accountService;

	@Test
	public void payment() {
		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setAmount(new BigDecimal(10));
		accountDTO.setOrderId(123L);
		accountDTO.setUserId(1L);
		accountService.payment(accountDTO);
	}

	@Test
	public void cancelPayment() {
		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setAmount(new BigDecimal(10));
		accountDTO.setOrderId(123L);
		accountDTO.setUserId(1L);

		accountService.cancelPayment(accountDTO);
	}

	@Test
	public void findByUserId() {

	}
}