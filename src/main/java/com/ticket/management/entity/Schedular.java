package com.ticket.management.entity;

import com.ticket.management.entity.enums.SchedularEventType;
import com.ticket.management.entity.enums.SchedularType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@Table (name = "schedulars")
public class Schedular {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private SchedularType type;

    @Enumerated(EnumType.STRING)
    @Column(name="event_type", nullable = false)
    private SchedularEventType eventType;

    @Column(name="start_time", nullable = true)
    private Long startTime;

    @Column (name="periodicity", nullable = false)
    private Long periodicity;
}
