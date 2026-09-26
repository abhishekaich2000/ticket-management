package com.ticket.management.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.enums.TicketStatus;

import java.util.Optional;
import java.util.Collection;
import java.util.List;
import java.time.LocalDateTime;

public interface TicketRepository extends JpaRepository<Ticket, Long>,  JpaSpecificationExecutor<Ticket> {
   
    @EntityGraph(attributePaths = {"customer", "assignedAgent"})
    Page<Ticket> findAll(Specification<Ticket> spec, Pageable pageable);

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    @EntityGraph(attributePaths = {"customer", "assignedAgent"})
    List<Ticket> findByCustomerId(Long customerId);

    List<Ticket> findByAssignedAgentId(Long agentId);
    
    @EntityGraph(attributePaths = {"customer", "assignedAgent"})
    Optional<Ticket> findByIdAndCustomerId(Long id, Long customerId);

    List<Ticket> findBySlaDueAtLessThanAndIsSlaBreachedFalse(LocalDateTime currDateTime);

    // Get ticket with lowest/earliest SLA due date
    Optional<Ticket> findFirstBySlaDueAtGreaterThanEqualAndStatusNotInOrderBySlaDueAtAsc(
        LocalDateTime now,
        Collection<TicketStatus> statuses
    );

    Optional<Ticket> findFirstBySlaDueAtIsNotNullAndStatusNotInAndIsSlaBreachedFalseOrderBySlaDueAtAsc( Collection<TicketStatus> statuses);
}
