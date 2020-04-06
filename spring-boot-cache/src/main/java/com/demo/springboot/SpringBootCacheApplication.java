package com.demo.springboot;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author - Jhjing
 * @since - 2020.1.21 021
 * - 注解 @EnableCaching : 开启注解版的缓存功能，配合 @Cacheable 等注解
 * - 注解 @EnableRabbit ：开启 RabbitMQ，配合 @RabbitListener 注解
 * - 注解 @EnableAsync ：开启异步任务，配合 @Async 注解
 * - 注解 @EnableScheduling ：开启定时任务，配合 @Scheduled 注解
 */
@SpringBootApplication
@EnableCaching
@EnableRabbit
@EnableAsync
@EnableScheduling
public class SpringBootCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCacheApplication.class, args);
    }

}
