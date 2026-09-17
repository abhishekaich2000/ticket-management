package com.ticket.management.dto;

import com.ticket.management.entity.TicketCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketCategoryDto {

    @NotNull(message = "Category is required")
    private TicketCategory category;
}
