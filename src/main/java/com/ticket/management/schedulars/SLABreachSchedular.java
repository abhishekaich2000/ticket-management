package com.ticket.management.schedulars;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ticket.management.entity.Ticket;
import com.ticket.management.repository.TicketRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component 
@RequiredArgsConstructor  
@Slf4j 
public class SLABreachSchedular {

    private final TicketRepository ticketRepository;

    @Scheduled (initialDelay = 300000, fixedDelay = 300000)
    public void checkSlaBreach(){
        try {
            log.info("SLA Scheduler running...");
            List<Ticket> tickets = ticketRepository.findBySlaDueAtLessThan(LocalDateTime.now());

            if(tickets.isEmpty()){
                log.info("No sla breached tickets found");
                return;
            }
            log.warn("Total {} sla breached tickets found", tickets.size());
        } catch (Exception e) {
            log.error("Scheduler error: ", e);
        }
    }
}
