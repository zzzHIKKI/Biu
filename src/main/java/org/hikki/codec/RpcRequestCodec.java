package org.hikki.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.hikki.Serialization.Serializer;
import org.hikki.Serialization.SerializerFactory;
import org.hikki.transport.RpcRequest;

import java.util.List;

/**
 * Created by HIKKIさまon 2017/11/25 21:06
 * Description:.
 */
public class RpcRequestCodec extends ByteToMessageCodec<RpcRequest> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest, ByteBuf byteBuf) throws Exception {
        Serializer serializer = SerializerFactory.load();
        byte[] bytes = serializer.serialize(rpcRequest);
        int len = bytes.length;
        //先写入信息长度
        byteBuf.writeInt(len);
        //再写入信息的实体
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
        Object object = serializer.deserialize(bytes, RpcRequest.class);
        list.add(object);
    }
}
