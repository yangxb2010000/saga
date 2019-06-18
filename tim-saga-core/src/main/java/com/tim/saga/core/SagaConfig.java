package com.tim.saga.core;

import lombok.Builder;
import lombok.Data;
import oracle.jrockit.jfr.Recording;

/**
 * @author xiaobing
 * <p>
 * Saga配置类
 */
@Data
@Builder
public class SagaConfig {
    /**
     * 应用Id，用于多个使用用同一个DB的区分
     */
    private String applicationId;

    /**
     * 是否异步执行cancel
     */
    private boolean asyncCancel;

    /**
     * 是否并发执行Participant的cancel
     */
    private boolean concurrentCancel;

    /**
     * 等待并发cancel处理完成的超时时间
     */
    private int waitConcurrentCancelTimeoutInMs;

    /**
     * 回滚相关配置
     */
    @Builder.Default
    private RecoverConfig recoverConfig = RecoverConfig
            .builder()
            .recoverDuration(600)
            .maxRetryCount(10)
            .batchHandleTransactionCount(20).build();

    /**
     * 并发执行cancenl的线程池配置
     */
    @Builder.Default
    private ThreadPoolConfig threadPoolConfig = ThreadPoolConfig
            .builder()
            .blockingDeQueCapacity(2000)
            .coreSize(4)
            .maxSize(8)
            .keepAliveTimeInMs(1000)
            .build();

    @Data
    @Builder
    public static class RecoverConfig {
        /**
         * 事务未更新之后多长时间进行recover操作
         */
        private int recoverDuration;

        /**
         * 事务最大重试次数
         */
        private int maxRetryCount;

        /**
         * 一次任务处理多少个事务
         */
        private int batchHandleTransactionCount;

    }

    @Data
    @Builder
    public static class ThreadPoolConfig {
        /**
         * corePoolSize
         */
        private int coreSize = 2;

        /**
         * maximumPoolSize
         */
        private int maxSize = 8;
        /**
         * LinkedBlockingQueue capacity
         */
        private int blockingDeQueCapacity = 200;
        /**
         * keepAliveTime in ms
         */
        private int keepAliveTimeInMs = 30000;
    }
}
