package com.ticket.management.mq.consumer;

import java.util.Optional;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.ticket.management.config.RabbitMQConfig;
import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.User;
import com.ticket.management.events.TicketEvent;
import com.ticket.management.repository.TicketRepository;
import com.ticket.management.repository.UserRepository;
import com.ticket.management.service.TicketHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
@RequiredArgsConstructor 
public class TicketEventsConsumer {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketHistoryService ticketHistoryService;

    @RabbitListener (queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeTicketEvent(TicketEvent event) {
        try {
            log.info("Received Ticket Created Event: {}", event);
            test();
            Optional<Ticket> optionalTicket = ticketRepository.findById(event.getTicketId());
            if(!optionalTicket.isPresent()) {
                log.warn("Ticket with ID {} not found", event.getTicketId());
                return;
            }
            Optional<User> optionalUser = userRepository.findById(event.getUserId());
            if(!optionalUser.isPresent()) {
                log.warn("User with ID {} not found", event.getUserId());
                return;
            }
            Ticket ticket = optionalTicket.get();
            User user = optionalUser.get();
            ticketHistoryService.recordEvent(ticket, 
                user, event.getEntityType(), event.getEventType(),
                event.getOldValue(),event.getNewValue(), event.getTimestamp());
            log.info("Recorded ticket with entity {} with event {} for ticket ID {} by user ID {}", event.getEntityType(), event.getEventType(), event.getTicketId(), event.getUserId());
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

    private void test() throws Exception{
        throw new Exception("DLQ test");
    }
}
