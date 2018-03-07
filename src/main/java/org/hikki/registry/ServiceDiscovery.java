package org.hikki.registry;

import org.apache.log4j.Logger;
import org.hikki.registry.zookeeper.ZooKeeperManager;
import org.hikki.utils.RandomGenerator;

import java.util.List;

/**
 * Created by HIKKIさまon 2017/11/25 19:38
 * Description:.
 */
public class ServiceDiscovery {

    private static final Logger LOGGER = Logger.getLogger(ServiceDiscovery.class);

    private ZooKeeperManager manager;

    public ServiceDiscovery(ZooKeeperManager manager) {
        this.manager = manager;
    }

    public String discovey(){
        manager.connect();
        List<String> services = manager.getChildren(ZooKeeperManager.ZK_REGISTRY_PATH);
        int size = services.size();
        int index = RandomGenerator.randInt(0, size - 1);
        String connectString = services.get(index);
        LOGGER.info("Select connection [" + connectString + "] as service provider.");
        return connectString;
    }
}
