package com.ticket.management.dto;

import com.ticket.management.entity.TicketPriority;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketPriorityDto {

    @NotNull(message = "Priority is required")
    private TicketPriority priority;
}
