package com.demo.springboot.cache;

import lombok.Cleanup;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * @author - Jianghj
 * @since - 2020-04-08 21:05
 * 测试使用 Zookeeper 客户端 Curator 实现的分布式锁
 * InterProcessMutex：可重入的互斥公平锁
 */
@SpringBootTest
public class CuratorDistributeLockTest {

    @Test
    public void testInterProcessMutex(){
        // Zookeeper 地址
        final String connectString = "localhost:2181";

        // 重试策略：每次重试之间需要等待的时间，基准等待时间为1秒，最多尝试 3 次。
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        // 使用默话时间（60秒）和连接超时时间（15秒）来创建 Zookeeper 客户端
        @Cleanup
        CuratorFramework client = CuratorFrameworkFactory.builder().
                connectString(connectString).
                connectionTimeoutMs(15 * 1000).
                sessionTimeoutMs(60 * 1000).
                retryPolicy(retryPolicy).
                build();

        // 启动客户端
        client.start();

        // 目标锁
        final String lockNode = "/lock_node";
        // 创建互斥锁
        InterProcessMutex lock = new InterProcessMutex(client, lockNode);
        try {
            // 方式1. 申请锁 - 一直阻塞，直到获取到锁
            // lock.acquire();
            // OR
            // 方式2. 申请锁 - 一直阻塞，直到获取到锁或达到指定的过期时间
            if (lock.acquire(60, TimeUnit.MINUTES)) {
                //查看当前客户端创建的目标锁节点：存在，获取锁成功，否则失败
                Stat stat = client.checkExists().forPath(lockNode);
                if (null != stat){
                    // 不为null，表示申请到锁
                    // 执行代码逻辑
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //判断是否申请到了锁
            if (lock.isAcquiredInThisProcess()) {
                try {
                    //如果获取到了锁，就释放锁
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
