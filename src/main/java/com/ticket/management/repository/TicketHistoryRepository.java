package com.ticket.management.repository;

import com.ticket.management.entity.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long> {
    List<TicketHistory> findByTicketIdOrderByCreatedAtDesc(Long ticketId);
}
