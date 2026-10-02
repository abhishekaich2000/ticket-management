package com.ticket.management.ai.handler;

import com.ticket.management.entity.Ticket;
import com.ticket.management.messaging.event.TicketEvent;

public interface TicketAiHandler {
    boolean supports(TicketEvent ticketEvent);
    void handle(TicketEvent ticketEvent, Ticket ticket);
}
