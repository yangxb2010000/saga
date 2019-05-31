package com.tim.sagatransaction.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
/**
 * 启用Saga事务的标记
 */
public @interface SagaTransactional {
	String name();
}
