package org.hikki.client.proxy;

import org.apache.log4j.Logger;
import org.hikki.client.RpcClient;
import org.hikki.transport.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by HIKKIさまon 2017/11/25 19:22
 * Description:客户端代理，向调用方屏蔽细节。
 */
public class RpcProxy implements InvocationHandler{
    private static final Logger LOGGER = Logger.getLogger(RpcProxy.class);

    private Class<?> clazz;
    private RpcClient client;

    public RpcProxy(RpcClient client) {
        this.client = client;
    }
    public <T> T newProxy(Class<?> clazz){
        this.clazz = clazz;
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{clazz},this);
    }
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcResponse response = (RpcResponse) client.call(clazz, method, args);
        //这里的返回值为服务端的响应
        return response.getResult();
    }
}
