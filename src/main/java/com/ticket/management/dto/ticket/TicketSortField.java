package com.ticket.management.dto.ticket;

public enum TicketSortField {
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt"),
    PRIORITY("priority"),
    STATUS("status"),
    CATEGORY("category"),
    SLA_DUE_AT("slaDueAt");

    private final String fieldName;

    TicketSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
