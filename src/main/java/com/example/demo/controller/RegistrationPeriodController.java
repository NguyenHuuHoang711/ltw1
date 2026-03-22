package com.example.demo.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RegistrationPeriodRequest;
import com.example.demo.repository.RegistrationPeriodRepository;
  
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/periods")
@RequiredArgsConstructor
public class RegistrationPeriodController {

    private final RegistrationPeriodRepository repository;

    @PostMapping
    public ResponseEntity<?> createPeriod(@Valid @RequestBody RegistrationPeriodRequest request) {
        // Validate bổ sung: end_time phải sau start_time
        if (request.getEndTime().isBefore(request.getStartTime())) {
            return ResponseEntity.badRequest().body("Thời gian kết thúc phải sau thời gian bắt đầu");
        }
        
        // Map DTO sang Entity và Save (Nên dùng MapStruct)
        return ResponseEntity.status(HttpStatus.CREATED).body("Đã tạo đợt đăng ký mới");
    }
}
