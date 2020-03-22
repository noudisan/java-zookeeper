package com.dztt.util;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by zhoutaotao on 2020/3/22.
 */
public class ZKClientTest {

    //private static final String zkPath = "master:2181,slave1:2181,slave2:2181/zfpt";
    private static final String zkPath = "127.0.0.1:2181";
    private static ZkClient zkClient = null;

    @Before
    public void init() {
        int sessionTimeout = 10000;
        int connectionTimeout = 10000;
        zkClient = new ZkClient(zkPath, sessionTimeout, connectionTimeout);
    }

    @Test
    public void create() {
        // 创建节点
        String result = zkClient.create("/aa4", "test", CreateMode.EPHEMERAL);
        System.out.println(result);

        // 递归创建
        zkClient.createPersistent("/trade/open", true);

        // 注意不要写成这种，API的问题，这种无法递归创建
        // zkClient.createPersistent("/trade/open",true);
    }

    @Test
    public void delete() {
        // 递归删除
        Boolean results = zkClient.deleteRecursive("/trade");
        System.out.println("删除结果:" + results);
    }


    /**
     * 获取子节点
     */
    @Test
    public void getChildren() {
        List<String> childrenList = zkClient.getChildren("/trade");
        System.out.println(childrenList);
    }

    /**
     * 读取书记
     */
    @Test
    public void readData() {
        String data = zkClient.readData("/trade");
        System.out.println(data);
    }


    @Test
    public void setData() {
        String oldValue = zkClient.readData("/trade");
        System.out.println("获取前:" + oldValue);

        zkClient.writeData("/trade", "I am trade");

        String newValue = zkClient.readData("/trade");
        System.out.println("更新后:" + newValue);
    }

    @Test
    public void watch() {
        //监听子节点变化
        zkClient.subscribeChildChanges("/trade", (parentPath, currenChilds) -> {
            System.out.println("子节点发生变化");
        });

        zkClient.subscribeDataChanges("/trade", new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("dataPath:" + dataPath + "发生变化，最新数据是:" + data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.printf("dataPath被删除");
            }
        });

    }

}
