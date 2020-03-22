package com.dztt.util;

import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

/**
 * Created by zhoutaotao on 2020/3/22.
 */
public class CuratorNodeCacheTest extends CuratorTest {

    /**
     * NodeCache即可以用于监听指定ZooKeeper数据节点内容的变化，也能监听指定节点是否存在，
     * 如果原本节点不存在，那么Cache就会在节点被创建后出发NodeCacheListener。
     * 但是如果该数据节点被删除，那么Curator就无法再出发NodeCacheListener了。
     * @throws Exception
     */
    @Test
    public void NodeCacheTest() throws Exception {

        // client : Curator 客户端实例 。 path: 监听节点的节点路径 。 dataIsCompressed：是否进行数据压缩
        NodeCache nodeCache = new NodeCache(curatorFramework, "/trade", false);
        // buildInitial：如果设置为true 则NodeCache在第一次启动的时候就会立刻从ZK上读取对应节点的数据内容 保存到Cache中。
        nodeCache.start(false);
        nodeCache.getListenable().addListener(() -> {
            System.out.println("Node data update , new data:" + new String(nodeCache.getCurrentData().getData()));
        });


        //******************** 监听一个不存在的节点 当节点被创建后，也会触发监听器 **********************//
        // client : Curator 客户端实例 。 path: 监听节点的节点路径 。 dataIsCompressed：是否进行数据压缩
        NodeCache nodeCache2 = new NodeCache(curatorFramework, "/trade1", false);
        // buildInitial：如果设置为true 则NodeCache在第一次启动的时候就会立刻从ZK上读取对应节点的数据内容 保存到Cache中。
        nodeCache2.start(false);
        nodeCache2.getListenable().addListener(() -> {
            System.out.println("Node data update , new data:" + new String(nodeCache.getCurrentData().getData()));
        });
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 用于监听指定ZooKeeper数据节点的子节点变化情况。当指定节点的子节点发生变化时，就会回调该方法。
     * PathChildrenCacheEvent类中定义了所有的事件类型，
     * 主要包括新增子节点(CHILD_ADDED)、子节点数据变更(CHILD_UPDATED)和子节点删除(CHILD_REMOVED)三类。
     * 但是该数据节点的变化不会被此监听器监听到。无法监听孙子节点的变更。
     * @throws Exception
     */
    @Test
    public void PathChildrenCacheTest() throws Exception {
        PathChildrenCache nodeCache = new PathChildrenCache(curatorFramework, "/trade", true);
        // buildInitial：如果设置为true 则NodeCache在第一次启动的时候就会立刻从ZK上读取对应节点的数据内容 保存到Cache中。
        nodeCache.start();
        nodeCache.getListenable().addListener((client, event) -> {
            switch (event.getType()) {
                case CHILD_ADDED:
                    System.out.println("新增子节点,数据内容是" + new String(event.getData().getData()));
                    break;
                case CHILD_UPDATED:
                    System.out.println("子节点被更新,数据内容是" + new String(event.getData().getData()));
                    break;
                case CHILD_REMOVED:
                    System.out.println("删除子节点,数据内容是" + new String(event.getData().getData()));
                    break;
                default:
                    break;
            }
        });
        curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath("/trade/PathChildrenCache", "new".getBytes());
        Thread.sleep(100L);
        curatorFramework.setData().forPath("/trade/PathChildrenCache", "update".getBytes());
        Thread.sleep(100L);
        curatorFramework.delete().withVersion(-1).forPath("/trade/PathChildrenCache");
    }
}
