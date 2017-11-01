package com.yoogurt.taxi.common.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class HessianRedisSerializer<T> implements RedisSerializer<T> {

    /**
     * Serialize the given object to binary data.
     *
     * @param t object to serialize
     * @return the equivalent binary data
     */
    @Override
    public byte[] serialize(T t) throws SerializationException {
        if(t == null) return new byte[0];
        Hessian2Output output = null;
        ByteArrayOutputStream os;
        try {
            os = new ByteArrayOutputStream();
            output = new Hessian2Output(os);
            output.setCloseStreamOnClose(true);
            output.writeObject(t);
            output.flush();
            return os.toByteArray();
        } catch (IOException e) {
            log.error("[Hessian2]对象序列化失败, {}", e);
        } finally {
            try {
                if(output != null) output.close();
            } catch (IOException e) {
                log.error("输出流关闭异常, {}", e);
            }
        }
        return new byte[0];
    }

    /**
     * Deserialize an object from the given binary data.
     *
     * @param bytes object binary representation
     * @return the equivalent object instance
     */
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if(bytes == null || bytes.length <= 0) return null;
        Hessian2Input input = null;
        ByteArrayInputStream is;
        try {
            is = new ByteArrayInputStream(bytes);
            input = new Hessian2Input(is);
            input.setCloseStreamOnClose(true);
            return (T) input.readObject();
        } catch (IOException e) {
            log.error("[Hessian2]对象反序列化失败, {}", e);
        } finally {
            try {
                if(input != null) input.close();
            } catch (IOException e) {
                log.error("输入流关闭异常, {}", e);
            }
        }
        return null;
    }
}
