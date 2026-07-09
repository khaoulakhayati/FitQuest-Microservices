package com.fitquest.workout.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${fitquest.rabbitmq.exchange:fitquest.events}")
    private String exchangeName;

    @Bean
    public TopicExchange fitquestExchange() {
        return new TopicExchange(exchangeName, true, false);
    }
}
