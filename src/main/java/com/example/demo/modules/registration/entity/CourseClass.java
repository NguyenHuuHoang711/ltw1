
package com.example.demo.modules.registration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

// This is a placeholder entity. In a real application, it would be fully implemented.
@Data
@Entity
public class CourseClass {
    @Id
    private UUID id;
    private UUID courseId;
    private int credits;
    private String schedule;
    private int maxStudents;
    private int registeredStudents;
}
