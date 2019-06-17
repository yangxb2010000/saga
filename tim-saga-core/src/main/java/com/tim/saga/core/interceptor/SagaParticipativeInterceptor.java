package com.tim.saga.core.interceptor;

import com.tim.saga.core.InvocationContext;
import com.tim.saga.core.SagaApplicationContext;
import com.tim.saga.core.annotation.SagaParticipative;
import com.tim.saga.core.annotation.SagaTransactional;
import lombok.Builder;
import lombok.Data;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public class SagaParticipativeInterceptor {
    private static Logger logger = LoggerFactory.getLogger(SagaParticipativeInterceptor.class);

    public static <T> T intercept(SagaApplicationContext sagaApplicationContext, Supplier<T> readInvoke, AspectAnnotationInfo annotationInfo) {
        if (annotationInfo == null || annotationInfo.participativeAnnotation == null) {
            return readInvoke.get();
        }

        String participantName = annotationInfo.participativeAnnotation.name();
        if (StringUtils.isEmpty(participantName)) {
            participantName = annotationInfo.getClazz().getName() + ":" + annotationInfo.getMethod().getName();
        }

        Assert.notNull(annotationInfo.participativeAnnotation.cancelMethod(), "Cancel method for SagaParticipative cannot be null");

        InvocationContext cancelInvocationContext = new InvocationContext(annotationInfo.getClazz(), annotationInfo.participativeAnnotation.cancelMethod(), annotationInfo.getMethod().getParameterTypes(), annotationInfo.args);

        sagaApplicationContext.getTransactionManager().enlistParticipant(participantName, cancelInvocationContext);

        logger.debug("enlist Participant {} to transaction", participantName);

        return readInvoke.get();
    }

    @Data
    @Builder
    public static class AspectAnnotationInfo {
        private SagaParticipative participativeAnnotation;

        private Class clazz;

        private Method method;

        private Object[] args;
    }
}
