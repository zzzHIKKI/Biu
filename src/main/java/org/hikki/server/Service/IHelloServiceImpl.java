package org.hikki.server.Service;

import org.hikki.server.annotation.RPCService;

/**
 * Created by HIKKIさまon 2018/11/26 10:38
 * Description:.
 */
@RPCService(IHelloService.class)
public class IHelloServiceImpl implements IHelloService {
    public String hello(String value) {
        return "aloha! " + value;
    }
}
