package org.hikki.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.hikki.Serialization.Serializer;
import org.hikki.Serialization.SerializerFactory;

/**
 * Created by HIKKIさまon 2017/11/25 20:42
 * Description:.
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> clazz;

    public RpcEncoder(Class<?> clazz) {
        this.clazz = clazz;
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        Serializer serializer = SerializerFactory.load();
        byte[] bytes = serializer.serialize(o);
        int length = bytes.length;
        byteBuf.writeInt(length);
        byteBuf.writeBytes(bytes);
    }
}
