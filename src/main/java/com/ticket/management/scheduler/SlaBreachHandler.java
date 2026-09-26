package com.ticket.management.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.ticket.management.entity.Schedular;
import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.enums.SchedularEventType;
import com.ticket.management.entity.enums.TicketStatus;
import com.ticket.management.repository.TicketRepository;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class SlaBreachHandler implements SchedularJobHandler{

    private static final List<TicketStatus> INVALID_TICKET_STATUS =
        List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED);

    private final TicketRepository ticketRepository;
    private final SchedularService schedularService;

    public SlaBreachHandler(TicketRepository ticketRepository,
                            @Lazy SchedularService schedularService) {
        this.ticketRepository = ticketRepository;
        this.schedularService = schedularService;
    }

    @Override 
    public void handle(Schedular schedular) {
        log.info("SLA breach schedular is invoked");
        List<Ticket> tickets = ticketRepository.findBySlaDueAtLessThanAndIsSlaBreachedFalse(LocalDateTime.now());
        if (tickets.isEmpty()) {
            log.info("No sla breached tickets found");
            return;
        }
        log.warn("Total {} sla breached tickets found", tickets.size());
        List<Ticket> updated = tickets.stream()
            .peek(t -> t.setSlaBreached(true))
            .toList();
        ticketRepository.saveAll(updated);
        log.warn("Total {} sla breached tickets marked", updated.size());

        Optional<Ticket> remaining = ticketRepository
            .findFirstBySlaDueAtIsNotNullAndStatusNotInAndIsSlaBreachedFalseOrderBySlaDueAtAsc(INVALID_TICKET_STATUS);
        if (remaining.isEmpty()) {
            log.info("No valid tickets left for SLA monitoring — disabling schedular id={}",
                schedular.getId());
            schedularService.disable(schedular);
            return;
        }
    }

    @Override
    public SchedularEventType supports() {
        return SchedularEventType.SLA_BREACH;
    }
}
