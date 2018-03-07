package org.hikki.transport;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by HIKKIさまon 2018/11/25 20:23
 * Description:请求的对象，即从客户端发送到服务端的内容
 */
public class RpcRequest {
    //使用原子类作为requestID，也可以考虑使用zk生成全局唯一id
    private static final AtomicLong REQUEST_ID = new AtomicLong(1L);

    //请求id，全局唯一！！！
    private Long requestId;
    //类名
    private String className;
    //方法名
    private String methodName;
    //参数类型，为了应对重载方法
    private Class<?>[] paramTypes;
    //具体的参数
    private Object[] params;
    //每次请求，必定有一个唯一对应的id
    public RpcRequest() {
        this.requestId = REQUEST_ID.getAndIncrement();
    }
    //每次请求，必定有一个唯一对应的id
    // id由这边自己生成，并非是从客户端传入
    public RpcRequest(String className, String methodName,
                      Class<?>[] paramTypes, Object[] params) {
        this.requestId = REQUEST_ID.getAndIncrement();
        this.className = className;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.params = params;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}
