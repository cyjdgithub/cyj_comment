package com.ncy.y_comment.rabbitmq;

import com.ncy.y_comment.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String ROUTINGKEY = "seckill.message";

    public void send(String message) {
        log.info("send message: " + message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE,ROUTINGKEY, message);
    }
}
