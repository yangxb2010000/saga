package com.tim.sagatransaction.core.support;

import org.springframework.util.Assert;

/**
 * @author xiaobing
 */
public class BeanFactoryBuilder {
	private static BeanFactory beanFactory;

	/**
	 * 设置BeanFactory
	 *
	 * @param beanFactory
	 */
	public static void setBeanFactory(BeanFactory beanFactory) {
		Assert.notNull(beanFactory, "beanFactory should not be null");
		BeanFactoryBuilder.beanFactory = beanFactory;
	}

	public static BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
