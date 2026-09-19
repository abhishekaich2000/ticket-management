package com.ticket.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass // This annotation indicates that this class is a base class for all entities
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", insertable = true, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", insertable = true, updatable = true)
    private LocalDateTime updatedAt;

    // @Column(name="updated_by", insertable = false, updatable = false)
    // private String updatedBy;

    // @Column(name="created_by", insertable = false, updatable = false)
    // private String createdBy;
}
