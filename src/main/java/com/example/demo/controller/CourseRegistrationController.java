package com.example.demo.controller;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CourseRegistrationRequest;
import com.example.demo.entity.CourseRegistration;
import com.example.demo.repository.CourseRegistrationRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/registrations")
@RequiredArgsConstructor
public class CourseRegistrationController {

    private final CourseRegistrationRepository repository;

    @PostMapping
    public ResponseEntity<?> createRegistration(@Valid @RequestBody CourseRegistrationRequest request) {
        // Logic xử lý (Service) sẽ nằm ở đây
        // Ví dụ: Kiểm tra xem sinh viên đã đăng ký lớp này chưa, còn chỗ không...
        
        return ResponseEntity.ok("Đăng ký thành công!");
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CourseRegistration>> getByStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(repository.findByStudentId(studentId));
    }
}