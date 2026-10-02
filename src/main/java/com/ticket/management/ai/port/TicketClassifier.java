package com.ticket.management.ai.port;

import com.ticket.management.entity.Ticket;

public interface TicketClassifier {
    void classifyTicket(Ticket ticket);
}
