package com.tim.sagatransaction.core.support;

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

	/**
	 * 获取代理对象的原始对象
	 */
	<T> Class<T> getTargetBean(Object obj);
}
