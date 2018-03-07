package org.hikki.registry.zookeeper;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.hikki.utils.PropertyReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by HIKKIさまon 2017/11/25 19:39
 * Description:.
 */
public class ZooKeeperManager {
    private static final Logger LOGGER = Logger.getLogger(ZooKeeperManager.class);
    public static final int ZK_SESSION_TIMEOUT =
            Integer.parseInt(PropertyReader.getPropetty("zookeeper.properties","ZK_SESSION_TIMEOUT"));
    public static final String ZK_REGISTRY_PATH =
            PropertyReader.getPropetty("zookeeper.properties","ZK_REGISTRY_PATH");

    private String zkAddress;
    private ZooKeeper zk;

    public ZooKeeperManager(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public void connect(){
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            zk = new ZooKeeper(zkAddress, ZK_SESSION_TIMEOUT, new Watcher() {
                public void process(WatchedEvent watchedEvent) {
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            LOGGER.info("Has successfully connected to " + zkAddress);
        } catch (IOException e) {
            LOGGER.info("Has failed to connect to " + zkAddress);
            e.printStackTrace();
        } catch (InterruptedException e){
            LOGGER.info("Has failed to connect to " + zkAddress);
            e.printStackTrace();
        }
    }

    /**
     * 创建节点
     * @param nodePath
     */
    public void createNode(String nodePath){
        try {
            if (zk.exists(ZK_REGISTRY_PATH, true) == null){
                zk.create(ZK_REGISTRY_PATH,null, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
            zk.create(ZK_REGISTRY_PATH + "/" + nodePath, null,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            LOGGER.info("Node " + nodePath + " have been created successfully.");
            zk.close();
        } catch (KeeperException e) {
            LOGGER.error("Creating node " + nodePath + " failed.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            LOGGER.error("Creating node " + nodePath + " failed.");
            e.printStackTrace();
        }
    }

    public List<String> getChildren(String nodePath){
        checkPath(nodePath);
        List<String> children = new ArrayList<String>();
        try {
            List<String> results = zk.getChildren(nodePath, true);
            for (String result : results) {
                children.add(result);
            }
            zk.close();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return children;
    }

    public void deleteNode(String nodePath){
        checkPath(nodePath);
        try {
            //先删除子目录
            List<String> children = zk.getChildren(nodePath, true);
            for (String c : children) {
                zk.delete(nodePath + "/" + c, -1);
            }
            //再删除父目录
            zk.delete(nodePath, -1);
            zk.close();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkPath(String path){
        if (Strings.isNullOrEmpty(path)){
            String errorMsg = "group path can not be null or empty: " + path;
            LOGGER.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        if (!path.startsWith(ZK_REGISTRY_PATH)){
            String errorMsg = "Illegal access to group: " + path;
            LOGGER.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
    }
}
