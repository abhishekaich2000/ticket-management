package com.ticket.management.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration 
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "ticket-queue";
    public static final String EXCHANGE_NAME = "ticket-exchange";
    public static final String ROUTING_KEY = "ticket-routing-key";

    public static final String DLQ_QUEUE="ticket-dlq";
    public static final String DLQ_EXCHANGE="ticket-dlq-exchange";
    public static final String DLQ_ROUTING_KEY = "ticket-dlq-routing-key";

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
            .deadLetterExchange(DLQ_EXCHANGE)
            .deadLetterRoutingKey(DLQ_ROUTING_KEY)
            .build();
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
        .bind(queue)
        .to(exchange)
        .with(ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean 
    public Queue dlq(){
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean 
    public DirectExchange dqlExchange(){
        return new DirectExchange(DLQ_EXCHANGE);
    }

    @Bean 
    public Binding dlqBinding(Queue dlq, DirectExchange dlqExchange){
        return BindingBuilder
            .bind(dlq)
            .to(dlqExchange)
            .with(DLQ_ROUTING_KEY);
    }
}
