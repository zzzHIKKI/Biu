package org.hikki.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.hikki.Serialization.Serializer;
import org.hikki.Serialization.SerializerFactory;
import org.hikki.transport.RpcResponse;

import java.util.List;

/**
 * Created by HIKKIさまon 2017/11/25 21:09
 * Description:.
 */
public class RpcResponseCodec extends ByteToMessageCodec<RpcResponse> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse, ByteBuf byteBuf) throws Exception {
        Serializer serializer = SerializerFactory.load();
        byte[] bytes = serializer.serialize(rpcResponse);
        int len = bytes.length;
        byteBuf.writeInt(len);
        byteBuf.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        int len = byteBuf.readInt();
        if (byteBuf.readableBytes() < len) {
            throw new RuntimeException("Insufficient bytes to be read, expected: " + len);
        }
        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);
        Serializer serializer = SerializerFactory.load();
        Object object = serializer.deserialize(bytes, RpcResponse.class);
        list.add(object);
    }
}
