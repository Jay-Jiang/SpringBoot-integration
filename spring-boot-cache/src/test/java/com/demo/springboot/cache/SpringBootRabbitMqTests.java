package com.demo.springboot.cache;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author - Jianghj
 * @since - 2020-04-04 22:09
 * 测试 RabbitMQ
 */

@SpringBootTest
public class SpringBootRabbitMqTests {

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    AmqpAdmin amqpAdmin;

    @Test
    public void testAmqpAdmin() {
        // 在 RabbitMQ 默认虚拟主机中创建一个队列
        amqpAdmin.declareQueue(new Queue("demo.queue"));
        // 在 RabbitMQ 默认虚拟主机中创建一个 Direct 类型的转化器
        amqpAdmin.declareExchange(new DirectExchange("direct.exchange"));
        // 为 RabbitMQ 中的指定转换器添加一个新的绑定
        amqpAdmin.declareBinding(
                new Binding("demo.queue",
                        Binding.DestinationType.QUEUE,
                        "direct.exchange",
                        "demo.msg",
                        null));
    }

    @Test
    public void testSendMsg() {
        // 想指定的交换器发送消息，需要手动创建 Message 对象（消息头和消息头）
        // rabbitTemplate.send(exchange,routingKey, message);

        Map<String, Object> map = new HashMap<>(2);
        map.put("msg", "第一条推送消息");
        map.put("data", 1111);

        // 将需要发送的消息 object，自动转成 Message 中的消息体，并在序列之后，发送给RabbitMQ
        // 默认使用 java 的序列化方式，将 object 转成 byte[]，然后进行序列化
        // 修改序列化方式，如将数据保存成 JSON，需要自定义注入一个
        // rabbitTemplate.convertAndSend(exchange,routingKey, object);
        rabbitTemplate.convertAndSend("direct.exchange", "my.direct", map);
    }

    @Test
    public void testReceiveMsg() {
        // 接受指定序列中的消息
        rabbitTemplate.receive("demo.queue");
    }

}
