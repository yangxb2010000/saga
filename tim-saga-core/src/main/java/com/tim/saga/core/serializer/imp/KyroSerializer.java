package com.tim.saga.core.serializer.imp;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.tim.saga.core.exception.SagaException;
import com.tim.saga.core.serializer.ObjectSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KyroSerializer implements ObjectSerializer {

	/**
	 * 序列化对象
	 *
	 * @param obj
	 * @return
	 * @throws SagaException
	 */
	@Override
	public byte[] serialize(Object obj) throws SagaException {
		byte[] bytes;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); Output output = new Output(outputStream)) {
			//获取kryo对象
			Kryo kryo = new Kryo();
			kryo.writeObject(output, obj);
			bytes = output.toBytes();
			output.flush();
		} catch (IOException ex) {
			throw new SagaException("kryo serialize error" + ex.getMessage(), ex);
		}
		return bytes;
	}

	/**
	 * 反序列化对象
	 *
	 * @param param
	 * @param clazz
	 * @return
	 * @throws SagaException
	 */
	@Override
	public <T> T deSerialize(byte[] param, Class<T> clazz) throws SagaException {
		T object;
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(param)) {
			Kryo kryo = new Kryo();
			Input input = new Input(inputStream);
			object = kryo.readObject(input, clazz);
			input.close();
		} catch (IOException e) {
			throw new SagaException("kryo deSerialize error" + e.getMessage(), e);
		}
		return object;
	}

	/**
	 * 设置scheme.
	 *
	 * @return scheme 命名
	 */
	@Override
	public String getScheme() {
		return null;
	}
}
