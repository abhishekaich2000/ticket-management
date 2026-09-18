package com.ticket.management.service;

import com.ticket.management.entity.TicketHistory;
import com.ticket.management.entity.TicketEventType;
import com.ticket.management.entity.User;
import com.ticket.management.entity.Ticket;
import com.ticket.management.repository.TicketHistoryRepository;
import com.ticket.management.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketHistoryService {

    private final TicketHistoryRepository historyRepository;

    @Transactional
    public void recordEvent(Ticket ticket, String entityType, TicketEventType eventType,
                           String oldValue, String newValue) {
        User actor = SecurityUtil.getAuthenticatedUser();

        TicketHistory history = new TicketHistory();
        history.setTicket(ticket);
        history.setActor(actor);
        history.setEntityType(entityType);
        history.setEventType(eventType);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);

        historyRepository.save(history);
    }

    public List<TicketHistory> getTicketHistory(Long ticketId) {
        return historyRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);
    }
}
