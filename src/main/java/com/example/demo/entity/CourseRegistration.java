package com.example.demo.entity;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "course_registrations")
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseRegistration extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID studentId;
    private UUID courseClassId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_period_id")
    private RegistrationPeriod registrationPeriod;

    private Byte registrationType;
    private UUID replacedGradeId;
    private LocalDateTime registeredAt;
    private Integer status;
    private Boolean isPaid;

    @Version // Xử lý Optimistic Locking cho row_version
    @Column(name = "row_version", columnDefinition = "rowversion")
    private byte[] rowVersion;
}
