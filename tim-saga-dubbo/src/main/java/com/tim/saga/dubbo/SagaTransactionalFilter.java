package com.tim.saga.dubbo;

import com.tim.saga.core.SagaApplicationContext;
import com.tim.saga.core.annotation.SagaTransactional;
import com.tim.saga.core.exception.SagaException;
import com.tim.saga.core.interceptor.SagaTransactionalInterceptor;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 *
 */
@Activate(group = {Constants.CONSUMER})
public class SagaTransactionalFilter implements Filter {

    private SagaApplicationContext sagaApplicationContext;

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Class clazz = invoker.getInterface();
        String methodName = invocation.getMethodName();

        Method interfaceMethod;
        try {
            interfaceMethod = clazz.getMethod(methodName, invocation.getParameterTypes());
        } catch (NoSuchMethodException e) {
            String err = MessageFormat.format("cannot find method {0} from interface {1}", methodName, clazz.getName());
            throw new SagaException(err, e);
        }

        SagaTransactional sagaTransactional = interfaceMethod.getAnnotation(SagaTransactional.class);

        SagaTransactionalInterceptor.AspectAnnotationInfo annotationInfo = SagaTransactionalInterceptor.AspectAnnotationInfo.builder()
                .transactionalAnnotation(sagaTransactional)
                .clazz(clazz)
                .method(interfaceMethod)
                .build();

        return SagaTransactionalInterceptor.intercept(this.sagaApplicationContext, () -> {
            try {
                return invoker.invoke(invocation);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }, annotationInfo);
    }

    public void setSagaApplicationContext(SagaApplicationContext sagaApplicationContext) {
        this.sagaApplicationContext = sagaApplicationContext;
    }
}
