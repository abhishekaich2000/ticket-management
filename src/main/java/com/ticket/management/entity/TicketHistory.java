package com.ticket.management.entity;

import com.ticket.management.entity.enums.TicketEntityType;
import com.ticket.management.entity.enums.TicketEventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ticket_history")
@Getter
@Setter
public class TicketHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = true)
    private User actor;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private TicketEntityType entityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private TicketEventType eventType;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;
}
