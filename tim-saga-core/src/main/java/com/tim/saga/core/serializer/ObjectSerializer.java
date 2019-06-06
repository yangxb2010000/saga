package com.tim.saga.core.serializer;

import com.tim.saga.core.exception.SagaException;

/**
 * @author xiaobing
 */
public interface ObjectSerializer {

	/**
	 * 序列化对象
	 *
	 * @param obj
	 * @return
	 * @throws SagaException
	 */
	byte[] serialize(Object obj) throws SagaException;


	/**
	 * 反序列化对象
	 *
	 * @param param
	 * @param clazz
	 * @param <T>
	 * @return
	 * @throws SagaException
	 */
	<T> T deSerialize(byte[] param, Class<T> clazz) throws SagaException;

	/**
	 * 设置scheme.
	 *
	 * @return scheme 命名
	 */
	String getScheme();
}
