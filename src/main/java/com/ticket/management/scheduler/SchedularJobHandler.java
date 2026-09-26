package com.ticket.management.scheduler;

import com.ticket.management.entity.Schedular;
import com.ticket.management.entity.enums.SchedularEventType;

public interface SchedularJobHandler {

    SchedularEventType supports();
    void handle(Schedular schedular);
    
}
