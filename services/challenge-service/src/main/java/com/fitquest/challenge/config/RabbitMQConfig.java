package com.fitquest.challenge.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EVENTS_QUEUE = "challenge.user.events";

    @Value("${fitquest.rabbitmq.exchange:fitquest.events}")
    private String exchangeName;

    @Value("${fitquest.rabbitmq.queue.workout-completed:challenge.workout.completed}")
    private String workoutCompletedQueueName;

    @Bean
    public TopicExchange fitquestExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue userEventsQueue() {
        return new Queue(USER_EVENTS_QUEUE, true);
    }

    @Bean
    public Binding userRegisteredBinding(@Qualifier("userEventsQueue") Queue userEventsQueue, TopicExchange fitquestExchange) {
        return BindingBuilder.bind(userEventsQueue)
                .to(fitquestExchange)
                .with("user.registered");
    }

    @Bean
    public Queue workoutCompletedQueue() {
        return new Queue(workoutCompletedQueueName, true);
    }

    @Bean
    public Binding workoutCompletedBinding(@Qualifier("workoutCompletedQueue") Queue workoutCompletedQueue, TopicExchange fitquestExchange) {
        return BindingBuilder.bind(workoutCompletedQueue)
                .to(fitquestExchange)
                .with("workout.completed");
    }
}
