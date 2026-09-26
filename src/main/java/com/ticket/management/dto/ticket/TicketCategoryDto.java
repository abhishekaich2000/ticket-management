package com.ticket.management.dto.ticket;

import com.ticket.management.entity.enums.TicketCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketCategoryDto {

    @NotNull(message = "Category is required")
    private TicketCategory category;
}
