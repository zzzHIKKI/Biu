package org.hikki.transport;

/**
 * Created by HIKKIさまon 2017/11/25 20:34
 * Description:服务端的响应，需要注意的是，结果中带着请求id
 */
public class RpcResponse {
    private Long requestId;
    /**
     * 返回的结果可能是正确的结果也可能是异常
     */
    private Throwable error;
    private Object result;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "requestId='" + requestId + '\'' +
                ", error=" + error +
                ", result=" + result +
                '}';
    }
}
