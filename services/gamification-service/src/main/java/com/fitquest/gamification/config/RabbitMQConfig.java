package com.fitquest.gamification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String GAMIFICATION_QUEUE = "gamification-service.events";

    @Value("${fitquest.rabbitmq.exchange:fitquest.events}")
    private String exchangeName;

    @Value("${fitquest.rabbitmq.routing-key.workout-logged:workout.logged}")
    private String workoutLoggedKey;

    @Value("${fitquest.rabbitmq.routing-key.user-registered:user.registered}")
    private String userRegisteredKey;

    @Bean
    public TopicExchange fitquestExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue gamificationServiceQueue() {
        return QueueBuilder.durable(GAMIFICATION_QUEUE).build();
    }

    @Bean
    public Binding workoutLoggedBinding(Queue gamificationServiceQueue, TopicExchange fitquestExchange) {
        return BindingBuilder.bind(gamificationServiceQueue).to(fitquestExchange).with(workoutLoggedKey);
    }

    @Bean
    public Binding userRegisteredBinding(Queue gamificationServiceQueue, TopicExchange fitquestExchange) {
        return BindingBuilder.bind(gamificationServiceQueue).to(fitquestExchange).with(userRegisteredKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
