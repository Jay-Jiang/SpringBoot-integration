package com.demo.springboot.config;

import com.demo.springboot.entity.Employee;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * @author - Jianghj
 * @since - 2020-04-03 22:02
 */
@Configuration
public class MyRedisConfiguration {

    /**
     * 通过绑定指定类型，进行特殊序列化
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<Object, Employee> empRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Employee> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //自定义对象的序列化器
        Jackson2JsonRedisSerializer<Employee> employeeRedisSerializer = new Jackson2JsonRedisSerializer<>(Employee.class);
        template.setDefaultSerializer(employeeRedisSerializer);
        return template;
    }

    /**
     * 通用的序列化器，将对象转成 JSON 格式，并添加对象的 @class 属性
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<Object, Object> genericRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //自定义对象的序列化器
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        template.setDefaultSerializer(serializer);
        return template;
    }

    /**
     * 自定义 RedisCacheManager，修改默认的序列化方法，将 key/value 转换成 json 格式，放入 redis 中
     *
     * @param redisConnectionFactory
     */
    @Bean
    public RedisCacheManager employeeRedisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        //配置自定义的 RedisCacheConfiguration
        RedisCacheConfiguration cacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        // 设置缓存过期时间为一天
                        .entryTtl(Duration.ofHours(1))
                        // 禁用缓存空值
                        .disableCachingNullValues()
                        // 设置序列化器（默认使用 JDK 的）
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.
                                // 设置CacheManager的值序列化方式为json序列化，可加入@Class属性
                                        fromSerializer(new GenericJackson2JsonRedisSerializer()));
        // 设置默认的 cache 组件
        return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(cacheConfiguration).build();
    }
}
