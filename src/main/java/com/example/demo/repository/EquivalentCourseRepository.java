package com.example.demo.repository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.EquivalentCourse;

@Repository
public interface EquivalentCourseRepository extends JpaRepository<EquivalentCourse, UUID> {

    // Tìm tất cả môn tương đương của một môn học gốc
    List<EquivalentCourse> findByOriginalCourseIdAndIsActiveTrue(UUID originalCourseId);

    // Kiểm tra xem hai môn có phải là tương đương không
    boolean existsByOriginalCourseIdAndEquivalentCourseIdAndIsActiveTrue(UUID originalId, UUID equivalentId);
}