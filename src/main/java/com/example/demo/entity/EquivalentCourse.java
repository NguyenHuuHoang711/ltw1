package com.example.demo.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "equivalent_courses")
@Data
public class EquivalentCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "original_course_id", nullable = false)
    private UUID originalCourseId;

    @Column(name = "equivalent_course_id", nullable = false)
    private UUID equivalentCourseId;

    @Column(name = "equivalence_type")
    private Integer equivalenceType; // 1: Thay thế hoàn toàn, 2: Song song

    @Column(name = "effect_date")
    private LocalDate effectDate;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "note", length = 500)
    private String note;

    // Audit fields
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;
}