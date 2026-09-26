package com.ticket.management.messaging.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.ticket.management.config.RabbitMQConfig;
import com.ticket.management.messaging.event.TicketEvent;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TicketEventsProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendTicketCreatedEvent(TicketEvent event) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            event
        );
    }
}
