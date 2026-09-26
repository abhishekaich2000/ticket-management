package com.ticket.management.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import com.ticket.management.entity.Schedular;
import com.ticket.management.entity.enums.SchedularEventType;
import com.ticket.management.entity.enums.SchedularType;
import com.ticket.management.repository.SchedularRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service  
@RequiredArgsConstructor  
@Slf4j 
public class SchedularService {

    private final TaskScheduler taskScheduler;
    private final SchedularRepository schedularRepository;
    private final Map<Long, ScheduledFuture<?>> scheduledTasks=new ConcurrentHashMap<>();
    private final List<SchedularJobHandler> jobHandlers;
    private Map<SchedularEventType, SchedularJobHandler> schedularHandlers;

    public void schedule(Schedular schedular){

        if(schedular.getId() == null){
            throw new IllegalArgumentException("Schedular id cannot be null");
        }
        cancel(schedular.getId());
        ScheduledFuture<?> future=null;
        if(schedular.getType() == SchedularType.ONE_TIME){
            future = taskScheduler.schedule(
                ()->execute(schedular), 
                Instant.ofEpochMilli(schedular.getStartTime()));
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
        SchedularJobHandler handler = schedularHandlers.get(schedular.getEventType());
        if (handler == null) {
            log.warn("No handler for {}", schedular.getEventType());
            return;
        }
        handler.handle(schedular);
    }

    public void cancel(Long schedularId){
        ScheduledFuture<?> future = scheduledTasks.remove(schedularId);
        if(future != null){
            future.cancel(true);
            log.warn("Schedular with id {} is cancelled", schedularId);
        }
    }

    public void disable(Schedular schedular){
        cancel(schedular.getId());
        schedular.setEnabled(false);
        schedularRepository.save(schedular);
        log.warn("Schedular with id {} is disabled", schedular.getId());
    }

    public void reschedule(Schedular schedular){
        schedule(schedular);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void restoreEnabledJobs() {
        List<Schedular> jobs = schedularRepository.findByEnabledTrue();
        for (Schedular job : jobs) {
            try {
                schedule(job);
                log.info("Restored schedular id={} eventType={}", job.getId(), job.getEventType());
            } catch (Exception e) {
                log.error("Failed to restore schedular id={}", job.getId(), e);
            }
        }
        log.info("Restored {} enabled schedulers", jobs.size()); 
    }


    @PostConstruct
    void initHandlers() {
        schedularHandlers = jobHandlers.stream()
            .collect(Collectors.toMap(SchedularJobHandler::supports, h -> h));
    }
}
