package com.tim.saga.core.support;

/**
 * @author xiaobing
 *
 * 定义BeanFactory，用于获取调用cancel方法的类的实例
 */
public interface BeanFactory {
	/**
	 * 根据T获取实例
	 *
	 * @param var1
	 * @param <T>
	 * @return
	 */
	<T> T getBean(Class<T> var1);
}
