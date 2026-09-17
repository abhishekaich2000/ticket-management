package com.ticket.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ticket.management.entity.Ticket;
import java.util.Optional;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long>{
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    List<Ticket> findByCustomerId(Long customerId);

    List<Ticket> findByAssignedAgentId(Long agentId);

    Optional<Ticket> findByIdAndCustomerId(Long id, Long customerId);
}
