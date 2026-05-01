
package com.example.demo.modules.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

// This is a placeholder interface. In a real application, it would be fully implemented.
@Repository
public interface CourseClassRepository extends JpaRepository<com.example.demo.modules.registration.entity.CourseClass, UUID> {
}
