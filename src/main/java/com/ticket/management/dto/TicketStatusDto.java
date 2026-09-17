package com.ticket.management.dto;

import com.ticket.management.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketStatusDto {

    @NotNull(message = "Status is required")
    private TicketStatus status;
}
