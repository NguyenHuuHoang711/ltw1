package com.example.demo.repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.RegistrationPeriod;

@Repository
public interface RegistrationPeriodRepository extends JpaRepository<RegistrationPeriod, UUID> {

    // Tìm các đợt đăng ký đang hoạt động
    List<RegistrationPeriod> findByIsActiveTrue();

    // Tìm các đợt đăng ký theo học kỳ
    List<RegistrationPeriod> findBySemesterId(UUID semesterId);

    // Kiểm tra xem hiện tại có nằm trong khoảng thời gian cho phép đăng ký không
    @Query("SELECT r FROM RegistrationPeriod r WHERE r.isActive = true " +
           "AND :now BETWEEN r.startTime AND r.endTime")
    List<RegistrationPeriod> findActivePeriodsAt(@Param("now") LocalDateTime now);
}