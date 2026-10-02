package com.ticket.management.messaging.helper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ticket.management.entity.Schedular;
import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.enums.SchedularEventType;
import com.ticket.management.entity.enums.SchedularType;
import com.ticket.management.entity.enums.TicketEntityType;
import com.ticket.management.entity.enums.TicketEventType;
import com.ticket.management.entity.enums.TicketStatus;
import com.ticket.management.messaging.event.TicketEvent;
import com.ticket.management.repository.SchedularRepository;
import com.ticket.management.repository.TicketRepository;
import com.ticket.management.scheduler.SchedularService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
@RequiredArgsConstructor 
public class SlaSchedulerProcessor {

    private final SchedularRepository schedularRepository;
    private final SchedularService schedularService;
    private final TicketRepository ticketRepository;

    private static List<TicketStatus> INVALID_TICKET_STATUS_SCHEDULE = List.of(TicketStatus.RESOLVED, TicketStatus.CLOSED);

    @Transactional 
    public void handleSlaSchedular(TicketEvent event){
        try{
            if(!isValidSlaEvent(event)){
                log.info("No valid SLA event found, thus skipping");
                return;
            }

            Optional<Schedular> schOptional = schedularRepository.findByEventType(SchedularEventType.SLA_BREACH);
            Optional<Ticket> ticketOpt = ticketRepository.findFirstBySlaDueAtIsNotNullAndStatusNotInAndIsSlaBreachedFalseOrderBySlaDueAtAsc(INVALID_TICKET_STATUS_SCHEDULE);
            
            if(!ticketOpt.isPresent()){
                schedularRepository.findByEventType(SchedularEventType.SLA_BREACH)
                .ifPresent(schedularService::disable);
                return;
            }

            long delayMs = ChronoUnit.MILLIS.between(LocalDateTime.now(), ticketOpt.get().getSlaDueAt());
            if (delayMs < 0) {
                delayMs = Math.abs(delayMs);
            }
            
            long minutes = Duration.ofMillis(delayMs).toMinutes();
            minutes = (long) (Math.sqrt(minutes));
            delayMs = Duration.ofMinutes(minutes).toMillis();
            Long starttime = System.currentTimeMillis() + delayMs;
            if(schOptional.isPresent()){
                Schedular job=schOptional.get();
                job.setEnabled(true);
                job.setType(SchedularType.PERIODIC);
                job.setPeriodicity(delayMs);
                job.setStartTime(starttime);
                schedularRepository.save(job);
                schedularService.reschedule(job);
                log.info("Rescheduled SLA_BREACH job id={} delayMs={}", job.getId(), delayMs);
                return;
            }

            Schedular job = new Schedular();
            job.setEventType(SchedularEventType.SLA_BREACH);
            job.setType(SchedularType.PERIODIC);
            job.setEnabled(true);
            job.setStartTime(starttime);
            job.setPeriodicity(delayMs);
            schedularRepository.save(job);
            schedularService.schedule(job);
            log.info("Created SLA_BREACH job id={} delayMs={}", job.getId(), delayMs);
        } catch (Exception e) {
            log.error("Error while handling SLA schedular: {}", e);
        }
    }

    private boolean isValidSlaEvent(TicketEvent event){
        TicketEntityType entityType = event.getEntityType();
        TicketEventType eventType = event.getEventType();
        return entityType == TicketEntityType.PRIORITY 
            || entityType == TicketEntityType.SLA
            || (entityType == TicketEntityType.TICKET && eventType == TicketEventType.CREATED);
    }
}
