package org.hikki.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.hikki.transport.RpcResponse;

/**
 * Created by HIKKIさまon 2017/11/25 21:12
 * Description:.
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private RpcResponse response;

    public RpcClientHandler(RpcResponse response) {
        this.response = response;
    }

    public RpcResponse getResponse() {
        return response;
    }

    /**
     * 读取服务端的响应
     * @param channelHandlerContext
     * @param rpcResponse
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        response.setRequestId(rpcResponse.getRequestId());
        response.setError(rpcResponse.getError());
        response.setResult(rpcResponse.getResult());
    }
}
