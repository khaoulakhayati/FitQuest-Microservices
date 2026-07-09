package com.fitquest.social.config;

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

    @Value("${fitquest.rabbitmq.exchange:fitquest.events}")
    private String exchangeName;

    @Value("${fitquest.rabbitmq.queue:social-service.events}")
    private String queueName;

    @Value("${fitquest.rabbitmq.routing-key.workout-logged:workout.logged}")
    private String workoutLoggedKey;

    @Value("${fitquest.rabbitmq.routing-key.achievement-unlocked:achievement.unlocked}")
    private String achievementUnlockedKey;

    @Value("${fitquest.rabbitmq.routing-key.challenge-completed:challenge.completed}")
    private String challengeCompletedKey;

    @Value("${fitquest.rabbitmq.routing-key.user-registered:user.registered}")
    private String userRegisteredKey;

    @Value("${fitquest.rabbitmq.routing-key.friend-request:friend.request}")
    private String friendRequestKey;

    @Bean
    public TopicExchange fitquestExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue socialServiceQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Binding workoutLoggedBinding(Queue socialServiceQueue, TopicExchange fitquestExchange) {
        return BindingBuilder.bind(socialServiceQueue).to(fitquestExchange).with(workoutLoggedKey);
    }

    @Bean
    public Binding achievementUnlockedBinding(Queue socialServiceQueue, TopicExchange fitquestExchange) {
        return BindingBuilder.bind(socialServiceQueue).to(fitquestExchange).with(achievementUnlockedKey);
    }

    @Bean
    public Binding challengeCompletedBinding(Queue socialServiceQueue, TopicExchange fitquestExchange) {
        return BindingBuilder.bind(socialServiceQueue).to(fitquestExchange).with(challengeCompletedKey);
    }

    @Bean
    public Binding userRegisteredBinding(Queue socialServiceQueue, TopicExchange fitquestExchange) {
        return BindingBuilder.bind(socialServiceQueue).to(fitquestExchange).with(userRegisteredKey);
    }

    @Bean
    public Binding friendRequestBinding(Queue socialServiceQueue, TopicExchange fitquestExchange) {
        return BindingBuilder.bind(socialServiceQueue).to(fitquestExchange).with(friendRequestKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
