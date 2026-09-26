package com.ticket.management.entity;

import com.ticket.management.entity.enums.TicketCategory;
import com.ticket.management.entity.enums.TicketPriority;
import com.ticket.management.entity.enums.TicketStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.FetchType;

@Entity
@Table(name = "tickets", indexes = {
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_assigned_to", columnList = "assigned_to"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_priority", columnList = "priority"),
    @Index(name = "idx_sla_due_at", columnList = "sla_due_at"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_ticket_number", columnList = "ticket_number")
})
@Getter 
@Setter 
public class Ticket extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ticket number cannot be blank")
    @Column(name = "ticket_number", nullable = false, unique = true)
    private String ticketNumber;

    @NotBlank(message = "Title cannot be blank")
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank(message = "Description cannot be blank")
    @Size(min = 10, max = 255, message = "Description must be less than 255 characters and greater than 10 characters")
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketStatus status;

    @NotNull(message = "Priority cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TicketPriority priority;

    @NotNull(message = "Category cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private TicketCategory category;

    @NotNull(message = "Customer cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to", nullable = true)
    private User assignedAgent;

    @Column(name = "resolved_at", nullable = true)
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at", nullable = true)
    private LocalDateTime closedAt;

    @Column(name = "sla_due_at", nullable = true)
    private LocalDateTime slaDueAt;

    @Column(name = "is_sla_breached", nullable = false)
    private boolean isSlaBreached = false;

    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore 
    private List<TicketComment> ticketComments = new ArrayList<>();

    @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore 
    private List<TicketHistory> ticketHistories=new ArrayList<>();

    // Helper methods
    public void addComment(TicketComment ticketComment){
        Assert.notNull(ticketComment, "Ticket comment cannot be null");
        ticketComment.setTicket(this);
        this.ticketComments.add(ticketComment);
    }

    public void removeComment(TicketComment comment) {
        Assert.notNull(comment, "Ticket comment cannot be null");
        this.ticketComments.remove(comment);
        comment.setTicket(null);
    } 

    public void addHistory(TicketHistory ticketHistory){
        Assert.notNull(ticketHistory, "Ticket history cannot be null");
        ticketHistory.setTicket(this);
        this.ticketHistories.add(ticketHistory);
    }

    public void removeHistory(TicketHistory history) {
        Assert.notNull(history, "Ticket history cannot be null");
        this.ticketHistories.remove(history);
        history.setTicket(null);
    }
}
