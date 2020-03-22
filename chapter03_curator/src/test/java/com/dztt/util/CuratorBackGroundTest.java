package com.dztt.util;

import org.apache.curator.shaded.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by zhoutaotao on 2020/3/22.
 */
public class CuratorBackGroundTest extends CuratorTest {

    @Test
    public void BackgroundCallbackTest() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(2);

        curatorFramework.getData().inBackground((client, event) -> {
            System.out.println(Thread.currentThread().getName());
            System.out.println(event);
            System.out.println(client);
        }).forPath("/trade");

        Executor executor = Executors.newFixedThreadPool(2, new ThreadFactoryBuilder().setNameFormat("curator.md-%d").build());
        curatorFramework.getData().inBackground((client, event) -> {
            System.out.println(Thread.currentThread().getName());
            System.out.println(event);
            System.out.println(client);
        }, executor).forPath("/trade");

        countDownLatch.await();
    }
}
