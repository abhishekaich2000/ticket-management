package com.ticket.management.entity;

public enum TicketEventType {
    CREATED,
    ASSIGNED,
    STATUS_CHANGED,
    PRIORITY_CHANGED,
    CATEGORY_CHANGED,
    COMMENTED,
    RESOLVED,
    CLOSED,
    REOPENED,
    SLA_BREACHED
}
