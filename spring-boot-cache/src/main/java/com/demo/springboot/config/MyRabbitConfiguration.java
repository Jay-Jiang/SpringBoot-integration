package com.demo.springboot.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author - Jianghj
 * @since - 2020-04-04 23:02
 * 自定义 RabbitMQ 消息序列化方式
 */
@Configuration
public class MyRabbitConfiguration {

    /**
     * 修改 RabbitMQ 消息序列化方式：JSON
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
