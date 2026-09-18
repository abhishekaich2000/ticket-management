package com.ticket.management.repository;

import com.ticket.management.entity.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
    List<TicketComment> findByTicketIdOrderByCreatedAtDesc(Long ticketId);
    List<TicketComment> findByTicketIdAndIsInternalFalseOrderByCreatedAtDesc(Long ticketId);
}
