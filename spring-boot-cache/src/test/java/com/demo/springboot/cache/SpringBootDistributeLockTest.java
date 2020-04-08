package com.demo.springboot.cache;

import com.demo.springboot.lock.RedisDistributeLock;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author - Jianghj
 * @since - 2020-04-08 11:42
 * 测试分布式锁
 */
@SpringBootTest
public class SpringBootDistributeLockTest {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void testGetLock() {
        String lockName = "demoLock";
        String lockMark = "demoLock001";
        RedisDistributeLock lock = new RedisDistributeLock(stringRedisTemplate, lockName, lockMark);
        try {
            //循环获取锁
            while (true) {
                if (lock.lockWithDaemon()) {
                    //获取到锁：执行业务代码
                    //查询所需信息，修改所需信息
                    System.out.println("get it!");
                    //结束循环
                    break;
                }
                //休眠一下
                TimeUnit.MICROSECONDS.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放锁
            lock.unlock();
        }
    }

}
