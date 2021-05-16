package com.tim.saga.core.interceptor;

import com.tim.saga.core.InvocationContext;
import com.tim.saga.core.SagaApplicationContext;
import com.tim.saga.core.annotation.SagaParticipative;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author xiaobing
 * <p>
 * 处理事务参与方的切面
 */
@Aspect
public class SagaParticipativeAspect {
    private static Logger logger = LoggerFactory.getLogger(SagaParticipativeAspect.class);

    private SagaApplicationContext sagaApplicationContext;

    public SagaParticipativeAspect(SagaApplicationContext sagaApplicationContext) {
        this.sagaApplicationContext = sagaApplicationContext;
    }

    @Pointcut("@annotation(com.tim.saga.core.annotation.SagaParticipative)")
    public void sagaParticipativePointcut() {
    }

    @Around("sagaParticipativePointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();

        SagaParticipative sagaParticipative = signature.getMethod().getAnnotation(SagaParticipative.class);

        SagaParticipativeInterceptor.AspectAnnotationInfo annotationInfo = SagaParticipativeInterceptor.AspectAnnotationInfo
                .builder()
                .participativeAnnotation(sagaParticipative)
                .clazz(signature.getDeclaringType())
                .method(signature.getMethod())
                .args(point.getArgs())
                .build();

        return SagaParticipativeInterceptor.intercept(this.sagaApplicationContext, () -> {
            try {
                return point.proceed();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }, annotationInfo);
    }
}
