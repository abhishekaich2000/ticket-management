package com.ticket.management.repository;

import com.ticket.management.entity.TicketComment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> , JpaSpecificationExecutor<TicketComment> {
    Page<TicketComment> findByTicketId(Long ticketId, Pageable pageable);
    Page<TicketComment> findByTicketIdAndIsInternalFalse(Long ticketId,Pageable pageable);
}
