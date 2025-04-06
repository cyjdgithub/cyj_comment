package com.ncy.y_comment.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE = "seckillQueue";
    public static final String EXCHANGE = "seckillExchange";
    public static final String ROUTING_KEY = "seckill.#";
    @Bean
    public Queue Queue() {
        return new Queue(QUEUE);
    }
    @Bean
    public TopicExchange TopicExchange() {
        return new TopicExchange(EXCHANGE);
    }
    @Bean
    public Binding Binding() {
        return BindingBuilder.bind(Queue()).to(TopicExchange()).with(ROUTING_KEY);
    }
}
