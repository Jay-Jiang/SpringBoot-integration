package com.demo.springboot.config;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author - Jianghj
 * @since - 2019-12-14 10:28
 * 通过注入自定义的 ConfigurationCustomizer 来配置 Mybatis
 */
@Configuration
public class MybatisConfiguration {

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            // 开启下划线命名和驼峰命名的自动映射
            configuration.setMapUnderscoreToCamelCase(true);
        };
    }
}
