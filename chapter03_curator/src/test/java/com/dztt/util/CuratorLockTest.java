package com.dztt.util;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.Test;

/**
 * Created by zhoutaotao on 2020/3/22.
 */
public class CuratorLockTest extends CuratorTest {

    /**
     * 观察  Lock【n】 抢到锁  和 Lock【n】 释放锁  是不是成对出现。 如果不是，则说明有重复加锁的
     *
     * @throws Exception
     */
    @Test
    public void InterProcessMutex() throws Exception {

        InterProcessMutex lock = new InterProcessMutex(curatorFramework, "/trade/mylock");

        for (int i = 0; i < 100; i++) {
            Thread currentThread = new Thread(() -> {
                try {
                    // 加锁
                    lock.acquire();
                    System.out.println(Thread.currentThread().getName() + " 抢到锁");
                } catch (Exception e) {
                } finally {
                    try {
                        System.out.println(Thread.currentThread().getName() + " 释放锁");
                        // 释放锁
                        lock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            currentThread.setName("Lock【" + i + "】");
            currentThread.start();
        }

        Thread.sleep(Integer.MAX_VALUE);
    }
}
