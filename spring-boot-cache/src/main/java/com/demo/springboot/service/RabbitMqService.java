package com.demo.springboot.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author - Jianghj
 * @since - 2020-04-04 22:48
 * 测试监听 RabbitMQ 中的队列，并获取消息
 * - 注意：需要在启动类上，添加 @EnableRabbit 注解
 */
@Service
public class RabbitMqService {

    /**
     * 监听指定的消息队列，并获取指定类型的消息体
     *
     * @param map
     */
    @RabbitListener(queues = {"direct.queue"})
    public void getMsg(Map map) {
        System.out.println(map);
    }

    /**
     * 监听指定的消息队列，并获取完整的消息（消息头+消息体）
     *
     * @param msg
     */
    @RabbitListener(queues = {"direct.queue"})
    public void getMsg(Message msg) {
        byte[] body = msg.getBody();
        MessageProperties header = msg.getMessageProperties();
        System.out.println(msg);
    }

}
