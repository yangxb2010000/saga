package com.tim.saga.core.support;

/**
 * @author xiaobing
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
