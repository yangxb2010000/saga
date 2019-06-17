package com.tim.saga.demo.dubbo.order.api;

import java.math.BigDecimal;

public interface OrderService {
    /**
     * 创建订单并且进行扣除账户余额支付，并进行库存扣减操作.
     *
     * @param count  购买数量
     * @param amount 支付金额
     * @return string
     */
    String orderPay(Integer count, BigDecimal amount);

    /**
     * 模拟在订单支付操作中，扣减库存异常.
     *
     * @param count  购买数量
     * @param price  单价
     * @return string
     */
    String mockInventoryWithException(Integer count, BigDecimal price);

    /**
     * 模拟在订单支付操作中，扣减库存时系统宕机.
     *
     * @param count  购买数量
     * @param price 单价
     * @return string
     */
    String mockInventoryWithShutdown(Integer count, BigDecimal price);
}
