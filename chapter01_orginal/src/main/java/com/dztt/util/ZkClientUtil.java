package com.dztt.util;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created by zhoutaotao on 2020/3/21.
 */
public class ZkClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(ZkClientUtil.class);
    private static ZooKeeper zk;
    //  /zfpt 必须提前创建好
    //private static String zkPath = "master:2181,slave1:2181,slave2:2181/zfpt";
    private static String zkPath = "127.0.0.1:2181";

    static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    static {
        try {
            zk = new ZooKeeper(zkPath, 1000, new Watcher() {
                // 监控所有被触发的事件
                public void process(WatchedEvent event) {
                    logger.info("已经触发了 {} 事件！ ", event.getType());
                    connectedSemaphore.countDown();
                }
            });
        } catch (Exception e) {
            logger.error("系统异常", e);
        }
    }

    public static ZooKeeper getZKConnection() {
        try {
            if (zk == null) {
                connectedSemaphore.await();
            }
            return zk;
        } catch (Exception e) {
            logger.error("ZK初始化失败", e);
        }
        return null;
    }
}
