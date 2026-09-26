package com.ticket.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket.management.entity.Schedular;
import com.ticket.management.entity.SchedularEventType;

public interface SchedularRepository extends JpaRepository<Schedular, Long>{

    Optional<Schedular> findByEventType(SchedularEventType eventType);
}
