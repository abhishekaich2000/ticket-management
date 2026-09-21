package com.ticket.management.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.ticket.management.entity.Ticket;
import com.ticket.management.entity.TicketCategory;
import com.ticket.management.entity.TicketPriority;
import com.ticket.management.entity.TicketStatus;

import jakarta.persistence.criteria.Predicate;

public class TicketSpecification {

    public static Specification<Ticket> filterByCriteria(
        TicketStatus status,
        TicketPriority priority,
        TicketCategory category,
        Long assignedAgentId,
        Long customerId
    ){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (priority != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }
            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }
            if (assignedAgentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("assignedAgent").get("id"), assignedAgentId));
            }
            if (customerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), customerId));
            }

            return predicates.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
