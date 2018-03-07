package org.hikki.registry;

import org.apache.log4j.Logger;
import org.hikki.registry.zookeeper.ZooKeeperManager;
//import org.slf4j.Logger;

/**
 * Created by HIKKIさまon 2017/11/25 19:39
 * Description:.
 */
public class ServiceRegistry {

    private static final Logger LOGGER = Logger.getLogger(ServiceRegistry.class);

    private ZooKeeperManager manager;

    public ServiceRegistry(ZooKeeperManager manager) {
        this.manager = manager;
    }

    /**
     * 注册服务到zk节点上
     * @param host
     * @param port
     */
    public void register(String host, int port) {
        manager.connect();
        manager.createNode(host + ":" + port);
        LOGGER.info("Register to " + host + ":" + port + " successfully");
    }

    /**
     * 服务器启动之前，删除所有已经注册的服务。
     */
    public void init() {
        manager.connect();
        manager.deleteNode(ZooKeeperManager.ZK_REGISTRY_PATH);
        LOGGER.info("Delete Node: " + ZooKeeperManager.ZK_REGISTRY_PATH);
    }
}
