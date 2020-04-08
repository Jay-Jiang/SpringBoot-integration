package com.demo.springboot.lock;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author - Jianghj
 * @since - 2020-04-07 22:07
 * Redis 实现分布式锁
 */
public class RedisDistributeLock {

    /** Redis模版 */
    private StringRedisTemplate stringRedisTemplate;
    /** Redis key：锁的名称 */
    private final String lockName;
    /** Redis value：锁的标记，用于识别当前线程和锁的关系 */
    private final String lockMark;
    /** 锁的超时时间，默认是 3 秒 */
    private final long timeout;
    /** 锁的守护线程 */
    private ScheduledFuture<?> daemonFuture;
    /** 守护线程启动的间隔时间：默认是过期时间的9/10 */
    private final long period;
    /** 时间单位：固定毫秒 */
    private final TimeUnit MILLISECOND = TimeUnit.MILLISECONDS;
    /** 锁的守护线程池 */
    private static ScheduledThreadPoolExecutor daemonThread = new ScheduledThreadPoolExecutor(3);

    /** 有参构造 */
    public RedisDistributeLock(StringRedisTemplate stringRedisTemplate, String lockName,
                               String lockMark, long timeout) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.lockName = lockName;
        this.lockMark = lockMark;
        this.timeout = timeout;
        this.period = timeout / 10 * 9;
    }

    /** 有参构造 */
    public RedisDistributeLock(StringRedisTemplate stringRedisTemplate, String lockName, String lockMark) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.lockName = lockName;
        this.lockMark = lockMark;
        this.timeout = 3000L;
        this.period = 2700L;
    }

    /**
     * 获取锁
     */
    public boolean lock() {
        /**
         * setIfAbsent 操作是原子的（底层使用 lua 脚本实现）
         * 如果 key 不存在，设置成功，同时设置 key 的过期时间，返回 true
         * 如果 key 存在，设置失败，返回 false
         */
        return stringRedisTemplate.opsForValue().setIfAbsent(lockName, lockMark, timeout, MILLISECOND);
    }

    /**
     * 获取拥有守护线程的锁，自动延长锁的过期时间
     */
    public boolean lockWithDaemon() {
        boolean get = lock();
        if (get) {
            startDaemon();
        }
        return get;
    }

    /**
     * 释放锁：建议放在 finally 代码块中使用
     */
    public void unlock() {
        try {
            if (daemonFuture != null) {
                //关闭守护线程
                daemonFuture.cancel(true);
            }
        } finally {
            // 如果当前创建的 lock 还没有过期
            if (lockMark.equals(stringRedisTemplate.opsForValue().get(lockName))) {
                //删除锁
                stringRedisTemplate.delete(lockName);
            }
        }
    }

    /**
     * 守护线程：如果锁即将到期，自动延长锁的过期时间
     * - 固定间隔执行更新任务：以 period 毫秒为固定周期，启动更新任务
     */
    private void startDaemon() {
        //启动守护进程
        daemonFuture = daemonThread.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                stringRedisTemplate.expire(lockName, timeout, MILLISECOND);
            }
        }, 0, period, MILLISECOND);
    }

}
