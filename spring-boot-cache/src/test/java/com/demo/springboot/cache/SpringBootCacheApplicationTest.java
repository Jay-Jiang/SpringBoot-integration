package com.demo.springboot.cache;

import com.demo.springboot.entity.Employee;
import com.demo.springboot.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;


@SpringBootTest
class SpringBootCacheApplicationTest {

    @Resource
    EmployeeService employeeService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    RedisTemplate empRedisTemplate;

    @Test
    void testObject() {
        Employee emp = employeeService.getEmp(1L);
        //直接保存对象（可序列化的），默认使用 JDK 序列化机制，将序列化之后的结果保存到 redis 中
        redisTemplate.opsForValue().set("emp-01",emp);
    }

    @Test
    void testObject2() {
        Employee emp = employeeService.getEmp(1L);
        //使用自定义的序列化器，来序列化指定对象
        empRedisTemplate.opsForValue().set("emp-01",emp);
    }

}
