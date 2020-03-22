package com.dztt.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.shaded.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhoutaotao on 2020/3/22.
 */
public class CuratoryBarrierTest extends CuratorTest {

    /**
     * 没有定义成员数量。直接通过removeBarrier();释放屏障
     *
     * @throws Exception
     */
    @Test
    public void barrier() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4, new ThreadFactoryBuilder().setNameFormat("barrier-%d").build());

        final DistributedBarrier[] distributedBarrier = new DistributedBarrier[1];

        for (int i = 0; i < 4; i++) {
            executor.execute(() -> {
                CuratorFramework client = CuratorFrameworkFactory.builder()
                        .connectString("master:2181,slave1:2181,slave2:2181")
                        .retryPolicy(new RetryOneTime(1000)) //重试策略
                        .namespace("zfpt") // 命名空间
                        .build();
                client.start();
                distributedBarrier[0] = new DistributedBarrier(curatorFramework, "/trade/PathChildrenCache");
                System.out.println(Thread.currentThread().getName() + "到达Barrier前");
                try {
                    distributedBarrier[0].setBarrier();
                    distributedBarrier[0].waitOnBarrier();
                    System.out.println(Thread.currentThread().getName() + "越过屏障");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(3000L);
        distributedBarrier[0].removeBarrier();
    }


    /**
     * 定义成员数量，到齐了就 越过屏障
     *
     * @throws Exception
     */
    @Test
    public void barrier2() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4, new ThreadFactoryBuilder().setNameFormat("barrier-%d").build());
        for (int i = 0; i < 4; i++) {
            executor.execute(() -> {
                CuratorFramework client = CuratorFrameworkFactory.builder()
                        .connectString("master:2181,slave1:2181,slave2:2181")
                        .retryPolicy(new RetryOneTime(1000)) //重试策略
                        .namespace("zfpt") // 命名空间
                        .build();
                client.start();

                DistributedDoubleBarrier distributedDoubleBarrier = new DistributedDoubleBarrier(client, "/trade/PathChildrenCache", 4);
                try {
                    Thread.sleep(1000L);
                    System.out.println(Thread.currentThread().getName() + "到达Barrier前");
                    distributedDoubleBarrier.enter();
                    System.out.println(Thread.currentThread().getName() + "越过屏障");
                    Thread.sleep(1000L);
                    distributedDoubleBarrier.leave();
                    System.out.println(Thread.currentThread().getName() + "已经离开");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(Integer.MAX_VALUE);
    }

}
