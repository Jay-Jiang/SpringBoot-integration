package com.demo.springboot;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 *@author - Jhjing
 *@since - 2020.1.21 021
 *注解 @EnableCaching : 开启注解版的缓存功能
 */
@SpringBootApplication
@EnableCaching
@EnableRabbit
public class SpringBootCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCacheApplication.class, args);
    }

}
