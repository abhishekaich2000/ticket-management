package com.ticket.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ticket.management.entity.Ticket;
import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

public interface TicketRepository extends JpaRepository<Ticket, Long>,  JpaSpecificationExecutor<Ticket> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    List<Ticket> findByCustomerId(Long customerId);

    List<Ticket> findByAssignedAgentId(Long agentId);

    Optional<Ticket> findByIdAndCustomerId(Long id, Long customerId);

    List<Ticket> findBySlaDueAtLessThan(LocalDateTime currDateTime);

    // Get ticket with lowest/earliest SLA due date
    Optional<Ticket> findFirstBySlaDueAtIsNotNullOrderBySlaDueAtAsc();
}
