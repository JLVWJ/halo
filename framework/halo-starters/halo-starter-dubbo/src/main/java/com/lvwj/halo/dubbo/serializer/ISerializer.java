package com.lvwj.halo.dubbo.serializer;

/**
 * 自定义序列化接口
 *
 * @author lvweijie
 * @date 2024年01月24日 12:18
 */
public interface ISerializer<T> {

    Object serialize(Object t);
}
