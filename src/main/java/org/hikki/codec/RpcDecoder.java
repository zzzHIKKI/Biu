package org.hikki.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.hikki.Serialization.Serializer;
import org.hikki.Serialization.SerializerFactory;
//import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created by HIKKIさまon 2017/11/25 20:56
 * Description:.
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    public RpcDecoder(Class<?> clazz) {
        this.clazz = clazz;
    }
    public void setClass(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //必定大于等于四个字节
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        Serializer serializer = SerializerFactory.load();
        //读取期望消息长度信息
        int length = byteBuf.readInt();
        //实际信息长度小于小心期望长度信息
        if (byteBuf.readableBytes() < length) {
            throw new RuntimeException("Insufficient bytes to be read, expected: " + length);
        }
        byte[] bytes = new byte[length];
        //将消息读入到字节数组中
        byteBuf.readBytes(bytes);
        //反序列化！！！
        Object object = serializer.deserialize(bytes, clazz);
        list.add(object);
    }
}
