package com.tim.saga.core.interceptor;

import com.tim.saga.core.SagaApplicationContext;
import com.tim.saga.core.annotation.SagaTransactional;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public class SagaTransactionalInterceptor {
    private static Logger logger = LoggerFactory.getLogger(SagaTransactionalInterceptor.class);

    public static <T> T intercept(SagaApplicationContext sagaApplicationContext, Supplier<T> readInvoke, AspectAnnotationInfo annotationInfo) {
        if (annotationInfo == null || annotationInfo.transactionalAnnotation == null) {
            return readInvoke.get();
        }

        String transactionName = annotationInfo.getTransactionalAnnotation().name();
        if (StringUtils.isEmpty(transactionName)) {
            transactionName = annotationInfo.getClazz().getName() + ":" + annotationInfo.getMethod().getName();
        }

        logger.debug("transaction begin for {}", transactionName);

        try {
            sagaApplicationContext.getTransactionManager().begin(transactionName);

            T res = readInvoke.get();

            sagaApplicationContext.getTransactionManager().commit();

            return res;

        } catch (Exception ex) {
            logger.debug("transaction rollback for {}", transactionName);

            try {
                sagaApplicationContext.getTransactionManager().rollback();
            } catch (Exception e) {
                //catch rollback事务日志，防止原来的业务异常被掩盖
                logger.error("failed to rollback transaction {}, will try again in recover job", transactionName, e);
            }

            throw ex;
        } finally {
            sagaApplicationContext.getTransactionManager().release();

            logger.debug("transaction release for ", transactionName);
        }
    }

    @Data
    @Builder
    public static class AspectAnnotationInfo {
        private SagaTransactional transactionalAnnotation;

        private Class clazz;

        private Method method;
    }
}
