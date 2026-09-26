package com.ticket.management.mq.consumer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.ticket.management.config.RabbitMQConfig;
import com.ticket.management.entity.Schedular;
import com.ticket.management.entity.SchedularEventType;
import com.ticket.management.entity.SchedularType;
import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.User;
import com.ticket.management.events.TicketEvent;
import com.ticket.management.repository.SchedularRepository;
import com.ticket.management.repository.TicketRepository;
import com.ticket.management.repository.UserRepository;
import com.ticket.management.schedulars.SchedularService;
import com.ticket.management.service.TicketHistoryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
@RequiredArgsConstructor 
public class TicketEventsConsumer {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketHistoryService ticketHistoryService;
    private final SchedularRepository schedularRepository;
    private final SchedularService schedularService;

    @RabbitListener (queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeTicketEvent(TicketEvent event) {
        try {
            log.info("Received Ticket Created Event: {}", event);
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

            handleSchedulingTicketSchedulars();
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

    @Transactional 
    private void handleSchedulingTicketSchedulars(){
        Optional<Schedular> schOptional = schedularRepository.findByEventType(SchedularEventType.SLA_BREACH);
        Ticket ticket = ticketRepository.findFirstBySlaDueAtIsNotNullOrderBySlaDueAtAsc().get();
        
        long delayMillis = 300000l;
        //long millisecondsUntilSLA = ChronoUnit.MILLIS.between(LocalDateTime.now(), ticket.getSlaDueAt());

        if(schOptional.isPresent()){
            Schedular schedular = schOptional.get();
            if(schedular.getPeriodicity() > delayMillis){
                schedular.setPeriodicity(delayMillis);
                schedularRepository.save(schedular);
            }
        }else{
            Schedular newSchedular = new Schedular();
            newSchedular.setEventType(SchedularEventType.SLA_BREACH);
            newSchedular.setType(SchedularType.PERIODIC);
            newSchedular.setStartTime(delayMillis);
            newSchedular.setPeriodicity(delayMillis);
            schedularRepository.save(newSchedular);
            schedularService.schedule(newSchedular);
        }
    }
}
