package com.dztt.util;

import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.RetryNTimes;
import org.junit.Test;

/**
 * Created by zhoutaotao on 2020/3/22.
 */
public class CuratorDistributeNumTest extends CuratorTest {

    @Test
    public void DistributedAtomicInteger() throws Exception {
        String counterPath = "/trade/PathChildrenCache";
        RetryNTimes retryPolicy = new RetryNTimes(1000, 3);
        DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(curatorFramework, counterPath, retryPolicy);

        System.out.println(atomicInteger.increment().postValue());
    }
}
