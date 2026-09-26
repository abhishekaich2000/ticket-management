package com.ticket.management.messaging.helper;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.User;
import com.ticket.management.messaging.event.TicketEvent;
import com.ticket.management.repository.TicketRepository;
import com.ticket.management.repository.UserRepository;
import com.ticket.management.service.TicketHistoryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Slf4j 
public class TicketEventProcessor {

    private final UserRepository userRepository;
    private final TicketHistoryService ticketHistoryService;
    private final TicketRepository ticketRepository;

    @Transactional 
    public void processTicketEvent(TicketEvent event){
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

        log.info("Recorded ticket with entity {} with event {} for ticket ID {} by user ID {}", event.getEntityType(), event.getEventType(), event.getTicketId(), event.getUserId());
    }
}
