package com.example.demo.entity;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "registration_periods")
@Data
@EqualsAndHashCode(callSuper = true)
public class RegistrationPeriod extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    
    @Column(name = "semester_id")
    private UUID semesterId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String targetConfig;

    private Integer maxCredits;
    private Integer minCredits;
    private Boolean allowRetake;
    private Boolean isActive;
}