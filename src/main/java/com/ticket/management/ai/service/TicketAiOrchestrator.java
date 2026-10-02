package com.ticket.management.ai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ticket.management.ai.handler.TicketAiHandler;
import com.ticket.management.entity.Ticket;
import com.ticket.management.messaging.event.TicketEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor
@Slf4j
public class TicketAiOrchestrator {

    private final List<TicketAiHandler> ticketAiHandlers;

    public void onTicketEvent(TicketEvent ticketEvent, Ticket ticket) {
        ticketAiHandlers.stream()
            .filter(handler -> handler.supports(ticketEvent))
            .forEach(handler -> handler.handle(ticketEvent, ticket));
        log.info("Ticket AI orchestrator processed ticket event: {}", ticketEvent);
    }
}
