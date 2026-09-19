package com.ticket.management.service;

import com.ticket.management.entity.TicketHistory;
import com.ticket.management.entity.TicketEventType;
import com.ticket.management.entity.User;
import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.TicketEntityType;
import com.ticket.management.repository.TicketHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketHistoryService {

    private final TicketHistoryRepository historyRepository;

    @Transactional
    public void recordEvent(Ticket ticket, User actor, TicketEntityType entityType, TicketEventType eventType,
                           String oldValue, String newValue, LocalDateTime createdAt) {

        TicketHistory history = new TicketHistory();
        history.setTicket(ticket);
        history.setActor(actor);
        history.setEntityType(entityType);
        history.setEventType(eventType);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setCreatedAt(createdAt);
        historyRepository.save(history);
    }

    public List<TicketHistory> getTicketHistory(Long ticketId) {
        return historyRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);
    }
}
