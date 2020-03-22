package com.dztt.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by zhoutaotao on 2020/3/22.
 */
public class CuratorTest {

    protected CuratorFramework curatorFramework;

    @Before
    public void before() {
        //  非Fluent风格
        //  CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(zkPath, new RetryOneTime(100));
        //  System.out.println(curatorFramework.getState());
        //  curatorFramework.start();
        //  System.out.println(curatorFramework.getState());
        // Fluent风格
        curatorFramework = CuratorFrameworkFactory.builder()
                //.connectString("master:2181,slave1:2181,slave2:2181")
                .connectString("127.0.0.1:2181")
                .retryPolicy(new RetryOneTime(1000)) //重试策略
                .namespace("zfpt") // 命名空间
                .build();
        curatorFramework.start();
    }

    @Test
    public void create() throws Exception {
        // 创建一个持久化节点，初始化内容为空
        curatorFramework.create().forPath("/dus");
        // 创建一个持久化节点，初始化内容不为空
        curatorFramework.create().forPath("/dus1", "test".getBytes());
        // 创建一个临时节点  初始化内容为空
        curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath("/dus2");
        // 创建一个临时节点，并递归创建不存在的父节点
        // ZooKeeper中规定所有非叶子节点必须为持久节点。因此下面创建出来只有dus2会是临时节点。
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/dj/dus2");
    }


    @Test
    public void delete() throws Exception {
        //删除一个节点
        curatorFramework.delete().forPath("/dus");
        // 删除一个节点，并递归删除其所有子节点
        curatorFramework.delete().deletingChildrenIfNeeded().forPath("/dus");
        // 删除一个节点，强制指定版本进行删除
        int version = 1;
        curatorFramework.delete().withVersion(version).forPath("/dus");
        //删除一个节点，强制保证删除成功
        curatorFramework.delete().guaranteed().forPath("/dus");
    }


    @Test
    public void read() throws Exception {
        // 读取一个节点的数据内容
        curatorFramework.getData().forPath("/dus");
        // 读取一个节点的数据内容，同时获取到该节点的stat
        Stat stat = new Stat();
        curatorFramework.getData().storingStatIn(stat).forPath("/dus");
    }

    @Test
    public void write() throws Exception {
        // 更新一个节点的数据内容
        curatorFramework.setData().forPath("/dus");
        // 更新一个节点的数据内容，强制指定版本进行更新
        int version = 1;
        curatorFramework.setData().withVersion(version).forPath("/dus");
    }





}
