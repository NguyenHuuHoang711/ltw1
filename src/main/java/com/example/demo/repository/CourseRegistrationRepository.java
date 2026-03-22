package com.example.demo.repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CourseRegistration;

@Repository
public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, UUID> {

    // Tìm tất cả phiếu đăng ký của một sinh viên
    List<CourseRegistration> findByStudentId(UUID studentId);

    // Tìm danh sách đăng ký theo đợt và trạng thái (VD: Đã thanh toán)
    List<CourseRegistration> findByRegistrationPeriodIdAndStatus(UUID periodId, Integer status);

    // Đếm số lượng sinh viên đã đăng ký vào một lớp môn học cụ thể
    // Dùng để kiểm tra sĩ số lớp
    long countByCourseClassIdAndStatusNot(UUID courseClassId, Integer statusCanceled);

    // Tìm phiếu đăng ký dựa trên sinh viên và lớp học (để tránh đăng ký trùng)
    Optional<CourseRegistration> findByStudentIdAndCourseClassId(UUID studentId, UUID courseClassId);
}
