package com.ticket.management.ai.handler;

import org.springframework.stereotype.Component;

import com.ticket.management.ai.port.TicketClassifier;
import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.enums.TicketEntityType;
import com.ticket.management.messaging.event.TicketEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TicketClassificationHandler implements TicketAiHandler {
   
    private final TicketClassifier ticketClassifier;

    @Override
    public boolean supports(TicketEvent ticketEvent) {
        return ticketEvent.getEntityType() == TicketEntityType.TICKET;
    }

    @Override
    public void handle(TicketEvent ticketEvent, Ticket ticket) {
        ticketClassifier.classifyTicket(ticket);
    }
}
