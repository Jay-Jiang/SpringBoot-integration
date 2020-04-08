package com.demo.springboot.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author - Jianghj
 * @since - 2020-04-08 21:40
 * 基于 Zookeeper 实现分布式锁
 * - 使用有序临时节点+注册监听（监听前一个节点）
 * - 使用com.github.sgroschupf.zkclient
 */
public class ZkClientDistributeLock {

    /** 路径分隔符 */
    private final String PATH_SEPARATOR = "/";
    /** Zookeeper 客户端 */
    private final ZkClient zkClient;
    /** 父节点（路径） */
    private final String parentNodePath;
    /** 当前锁节点的值（可以为空） */
    private final String lockNodeValue;
    /** 当前锁节点 */
    private String lockNode;
    /** 锁节点的前一个节点 */
    private String preNode;

    /** 有参构造 */
    public ZkClientDistributeLock(ZkClient zkClient, String parentName, String lockValue) {
        this.zkClient = zkClient;
        //判断父节点是否存在
        if (!zkClient.exists(parentName)) {
            // 如果不存在，就创建这个持久的父节点
            zkClient.createPersistent(parentName);
        }
        this.parentNodePath = initParentNodePath(parentName);
        this.lockNodeValue = lockValue == null ? "" : lockValue;
    }

    /**
     * 为父节点添加路径分隔符
     *
     * @param parentName 父节点
     */
    private String initParentNodePath(String parentName) {
        if (parentName.lastIndexOf(PATH_SEPARATOR) != parentName.length()) {
            parentName += PATH_SEPARATOR;
        }
        return parentName;
    }

    /**
     * 获取锁
     */
    public void lock() {
        if (!tryLock()) {
            //未获取到锁，阻塞并监听前一个节点
            waitAndWatch();
            //当前一个节点被删除，尝试重新上锁，逻辑最严谨
            // lock();
            //但是，如果阻塞结束，说明已经获取到锁，可以直接放行，执行任务代码
        }
    }

    /**
     * 阻塞线程，并注册 ZK 监听事件
     */
    private void waitAndWatch() {
        //创建门栓：用于阻塞线程
        final CountDownLatch latch = new CountDownLatch(1);
        //创建监听事件
        IZkDataListener listener = new CustomZkDataListener(latch);
        //注册监听事件：监听前一个节点的删除事件
        zkClient.subscribeDataChanges(preNode, listener);
        //如果前一个节点还存在，阻塞线程
        if (zkClient.exists(preNode)) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //当线程取消阻塞时，取消监听事件
        zkClient.unsubscribeDataChanges(preNode, listener);
    }

    /**
     * 释放锁
     */
    public void unlock() {
        //删除当前节点，释放锁
        zkClient.delete(lockNode);
    }

    /**
     * 尝试获取锁
     */
    public boolean tryLock() {
        if (lockNode == null) {
            //在父节点下，创建有序临时节点
            lockNode = zkClient.createEphemeralSequential(parentNodePath, lockNodeValue);
        }
        //获取父节点下的所有节点
        List<String> nodes = zkClient.getChildren(parentNodePath);
        //节点排序
        Collections.sort(nodes);
        //判断当前锁节点是否是最小的节点
        if (lockNode.equals(parentNodePath + nodes.get(0))) {
            //最小节点，获取锁成功
            return true;
        } else {
            //不是最小，则获取前一个节点，用于设置监听事件
            int preNodeIndex = nodes.indexOf(lockNode.substring(parentNodePath.length())) - 1;
            preNode = parentNodePath + nodes.get(preNodeIndex);
        }
        return false;
    }

    /**
     * 自定义实现 ZK 的数据监听器
     */
    private class CustomZkDataListener implements IZkDataListener {

        private CountDownLatch latch;

        CustomZkDataListener(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void handleDataChange(String s, Object o) throws Exception {

        }

        /**
         * 监听节点删除
         * - 触发时，表示获取到了锁
         *
         * @param s
         * @throws Exception
         */
        @Override
        public void handleDataDeleted(String s) throws Exception {
            //获取到锁，取消线程阻塞
            latch.countDown();
        }
    }
}
