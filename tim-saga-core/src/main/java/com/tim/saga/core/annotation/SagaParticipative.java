package com.tim.saga.core.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
/**
 * Saqa事务参与方的标记
 */
public @interface SagaParticipative {
	/**
	 * 取消事务的方法名
	 *
	 * @return
	 */
	String cancelMethod();

	/**
	 * 参与方的name
	 *
	 * @return
	 */
	String name() default "";
}
