package com.ticket.management.messaging.consumer;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.ticket.management.ai.service.TicketAiOrchestrator;
import com.ticket.management.config.RabbitMQConfig;
import com.ticket.management.messaging.event.TicketEvent;
import com.ticket.management.messaging.helper.SlaSchedulerProcessor;
import com.ticket.management.messaging.helper.TicketEventProcessor;
import com.ticket.management.repository.TicketRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
@RequiredArgsConstructor 
public class TicketEventsConsumer {

    private final TicketEventProcessor ticketEventProcessor;
    private final SlaSchedulerProcessor slaSchedulerProcessor;
    private final TicketAiOrchestrator ticketAiOrchestrator;
    private final TicketRepository ticketRepository;
    
    @RabbitListener (queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeTicketEvent(TicketEvent event) {
        try {
            ticketEventProcessor.processTicketEvent(event);
            slaSchedulerProcessor.handleSlaSchedular(event);
            ticketRepository.findById(event.getTicketId())
                .ifPresent(ticket -> ticketAiOrchestrator.onTicketEvent(event, ticket));
        } catch (Exception e) {
            log.error("Error while consuming ticket event {} with exception {}", event.getEventType(), e);
            throw new AmqpRejectAndDontRequeueException("Retry and DLQ", e);
        }
    }

    @RabbitListener (queues = RabbitMQConfig.DLQ_QUEUE)
    public void handleDLQ(TicketEvent event){
        log.error("Ticket event failed after max retires, so pushing in DLQ with event {} and ticketId {}", event.getEventType(), event.getTicketId());

        // TODO add audit and alert
    }
}
