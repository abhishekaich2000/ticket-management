package com.ticket.management.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.ticket.management.entity.Schedular;
import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.TicketHistory;
import com.ticket.management.entity.enums.SchedularEventType;
import com.ticket.management.entity.enums.TicketEntityType;
import com.ticket.management.entity.enums.TicketEventType;
import com.ticket.management.entity.enums.TicketStatus;
import com.ticket.management.repository.TicketHistoryRepository;
import com.ticket.management.repository.TicketRepository;
import com.ticket.management.service.TicketHistoryService;

import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class SlaBreachHandler implements SchedularJobHandler{

    private static final List<TicketStatus> INVALID_TICKET_STATUS =
        List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED);

    private final TicketRepository ticketRepository;
    private final SchedularService schedularService;
    private final TicketHistoryRepository ticketHistoryRepository;

    public SlaBreachHandler(TicketRepository ticketRepository,
                            @Lazy SchedularService schedularService,
                            TicketHistoryRepository ticketHistoryRepository) {
        this.ticketRepository = ticketRepository;
        this.schedularService = schedularService;
        this.ticketHistoryRepository = ticketHistoryRepository;
    }

    @Transactional
    @Override
    public void handle(Schedular schedular) {
        log.info("SLA breach schedular is invoked");
        List<Ticket> tickets = ticketRepository.findBySlaDueAtLessThanAndIsSlaBreachedFalse(LocalDateTime.now());
        if (tickets.isEmpty()) {
            log.info("No sla breached tickets found");
            return;
        }
        markTicketsAsSlaBreached(tickets);
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

    private void recordTicketHistories(List<Ticket> tickets){
        List<TicketHistory> histories = tickets.stream()
            .map(t -> {
                TicketHistory history = new TicketHistory();
                history.setTicket(t);
                history.setEntityType(TicketEntityType.SLA);
                history.setEventType(TicketEventType.SLA_BREACHED);
                return history;
            }).toList();
        ticketHistoryRepository.saveAll(histories);
        log.warn("Total {} ticket histories recorded", histories.size());
    }

    private void markTicketsAsSlaBreached(List<Ticket> tickets){
        log.warn("Total {} sla breached tickets found", tickets.size());
        List<Ticket> updated = tickets.stream()
            .peek(t -> {
                t.setSlaBreached(true);
                t.addHistory(createTicketHistory());
            })
            .toList();
        ticketRepository.saveAll(updated);
        log.warn("Total {} sla breached tickets marked", updated.size());
    }

    private TicketHistory createTicketHistory(){
        TicketHistory history = new TicketHistory();
        history.setEntityType(TicketEntityType.SLA);
        history.setEventType(TicketEventType.SLA_BREACHED);
        return history;
    }
}
