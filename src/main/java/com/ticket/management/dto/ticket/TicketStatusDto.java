package com.ticket.management.dto.ticket;

import com.ticket.management.entity.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketStatusDto {

    @NotNull(message = "Status is required")
    private TicketStatus status;
}
