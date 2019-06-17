package com.tim.saga.core.interceptor;

import com.tim.saga.core.SagaApplicationContext;
import com.tim.saga.core.annotation.SagaTransactional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author xiaobing
 * <p>
 * 处理定义事务的边界的切面
 */
@Aspect
public class SagaTransactionalAspect {
    private SagaApplicationContext sagaApplicationContext;

    public SagaTransactionalAspect(SagaApplicationContext sagaApplicationContext) {
        this.sagaApplicationContext = sagaApplicationContext;
    }

    @Pointcut("@annotation(com.tim.saga.core.annotation.SagaTransactional)")
    public void sagaTransactionalPointcut() {
    }

    @Around("sagaTransactionalPointcut()")
    public void around(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        SagaTransactional sagaTransactional = signature.getMethod().getAnnotation(SagaTransactional.class);

        SagaTransactionalInterceptor.AspectAnnotationInfo annotationInfo = SagaTransactionalInterceptor.AspectAnnotationInfo.builder()
                .transactionalAnnotation(sagaTransactional)
                .clazz(signature.getDeclaringType())
                .method(signature.getMethod())
                .build();

        SagaTransactionalInterceptor.intercept(this.sagaApplicationContext, () -> {
            try {
                return point.proceed();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }, annotationInfo);
    }
}
