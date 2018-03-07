package org.hikki.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.hikki.transport.RpcRequest;
import org.hikki.transport.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by HIKKIさまon 2017/11/26 9:52
 * Description:.
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private ConcurrentHashMap<String, Object> serviceMap;

    public RpcServerHandler(ConcurrentHashMap<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    /**
     * 读取到了来自客户端的请求，并作出响应！！！
     * @param channelHandlerContext
     * @param rpcRequest
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());//设置关键的requestId
        response.setError(null);
        Object result = handle(rpcRequest);
        response.setResult(result);
        //writeAndFlush省去了在channelReadComplete方法中调用ChannelHandlerContext的flush方法的过程
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     *
     * @param request
     * @return
     */
    private Object handle(RpcRequest request){
        String className = request.getClassName();
        Object service = serviceMap.get(className);
        //开始执行方法！！！
        Method[] methods = service.getClass().getDeclaredMethods();
        Object result = null;
        try {
            for (Method method : methods) {
                if (method.getName().equals(request.getMethodName())) {//在request中指定我要调用的方法
                    result = method.invoke(service, request.getParams());
                    //执行完这个方法就退出循环
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }
}
