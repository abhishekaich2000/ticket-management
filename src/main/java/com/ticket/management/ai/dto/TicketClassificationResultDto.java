package com.ticket.management.ai.dto;

import java.util.List;

import com.ticket.management.entity.enums.TicketCategory;

public record TicketClassificationResultDto(
    String category,
    int confidence,
    List<String> tags
) {
    public TicketCategory resolvTicketCategory() {
        if(category == null || category.isEmpty()) {
            return TicketCategory.GENERAL;
        }
        try {
            return TicketCategory.valueOf(category);
        } catch (IllegalArgumentException e) {
            return TicketCategory.GENERAL;
        }
    }
}

