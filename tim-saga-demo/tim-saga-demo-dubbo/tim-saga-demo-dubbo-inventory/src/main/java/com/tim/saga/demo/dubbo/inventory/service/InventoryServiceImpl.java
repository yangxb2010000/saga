package com.tim.saga.demo.dubbo.inventory.service;

import com.tim.saga.core.exception.SagaException;
import com.tim.saga.demo.dubbo.inventory.api.InventoryDTO;
import com.tim.saga.demo.dubbo.inventory.api.InventoryService;
import com.tim.saga.demo.dubbo.inventory.entity.InventoryDO;
import com.tim.saga.demo.dubbo.inventory.mapper.InventoryHistoryMapper;
import com.tim.saga.demo.dubbo.inventory.mapper.InventoryMapper;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
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
    public Integer findByProductId(String productId) {
        return inventoryMapper.findByProductId(productId).getInventoryCount();
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
