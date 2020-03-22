package com.dztt.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.shaded.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhoutaotao on 2020/3/22.
 */
public class CuratorleaderSelectorTest extends CuratorTest {


    @Test
    public void leaderSelector() throws Exception {
        AtomicInteger masterCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(4, new ThreadFactoryBuilder().setNameFormat("master_selector-%d").build());

        for (int i = 0; i < 4; i++) {
            executor.execute(() -> {
                LeaderSelector leaderSelector = new LeaderSelector(curatorFramework, "/master_selector", new LeaderSelectorListenerAdapter() {
                    @Override
                    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                        masterCount.incrementAndGet();

                        String name = Thread.currentThread().getName();
                        System.out.println(name + "成为Master, 当前Master数量：" + masterCount);

                        Thread.sleep(1000L);
                        System.out.println(name + "宕机，失去Master角色，剩下master数量：" + masterCount.decrementAndGet());
                    }
                });

                leaderSelector.autoRequeue();
                leaderSelector.start();
            });
        }
        Thread.sleep(Integer.MAX_VALUE);
    }
}
