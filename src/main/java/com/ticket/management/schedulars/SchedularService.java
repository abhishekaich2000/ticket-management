package com.ticket.management.schedulars;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import com.ticket.management.entity.Schedular;
import com.ticket.management.entity.SchedularType;
import com.ticket.management.entity.Ticket;
import com.ticket.management.repository.TicketRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service  
@RequiredArgsConstructor  
@Slf4j 
public class SchedularService {

    private final TaskScheduler taskScheduler;
    private final TicketRepository ticketRepository;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks=new ConcurrentHashMap<>();

    public void schedule(Schedular schedular){
        ScheduledFuture<?> future=null;
        if(schedular.getType() == SchedularType.ONE_TIME){
            future = taskScheduler.scheduleWithFixedDelay(
                ()->execute(schedular), 
                Duration.ofMillis(schedular.getStartTime()));
        }else{
            future = taskScheduler.scheduleWithFixedDelay(
                ()->execute(schedular), 
                Instant.ofEpochMilli(schedular.getStartTime()), 
                Duration.ofMillis(schedular.getPeriodicity()));
        }
        scheduledTasks.put(schedular.getId(), future);
    }

    private void execute(Schedular schedular){
        log.warn("Schedular with id {} with type {} with eventType {} is invoked", 
           schedular.getId(),schedular.getType(),schedular.getEventType());
        switch (schedular.getType()) {
            case ONE_TIME:
                handleOneTimeSchedular(schedular);
                break;
        
            case PERIODIC:
                handlePeriodicSchedular(schedular);
                break;

            default :
                log.warn("Schedular with id {} and type {} is not handled", 
                schedular.getId(), schedular.getType());
        }
        log.warn("Schedular with id {} is executed", schedular.getId());
    }

    private void handleOneTimeSchedular(Schedular schedular){

    }

    private void handlePeriodicSchedular(Schedular schedular){
        switch (schedular.getEventType()) {
            case SLA_BREACH:
                handleSLABreach(schedular);
                break;
        
            default:
                log.warn("Periodic Schedular with event type {} not handled", schedular.getEventType());
                break;
        }
    }

    private void handleSLABreach(Schedular schedular){
        log.info("SLA breach schedular is invoked");
        List<Ticket> tickets = ticketRepository.findBySlaDueAtLessThan(LocalDateTime.now());
        if(tickets.isEmpty()){
                log.info("No sla breached tickets found");
                return;
            }
        log.warn("Total {} sla breached tickets found", tickets.size());
    }
}
