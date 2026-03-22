package com.example.demo.dto;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RegistrationPeriodRequest {
    @NotBlank(message = "Tên đợt đăng ký không được để trống")
    @Size(max = 200, message = "Tên không được quá 200 ký tự")
    private String name;

    @NotNull(message = "Học kỳ không được để trống")
    private UUID semesterId;

    @NotNull(message = "Thời gian bắt đầu là bắt buộc")
    @FutureOrPresent(message = "Thời gian bắt đầu không được ở quá khứ")
    private LocalDateTime startTime;

    @NotNull(message = "Thời gian kết thúc là bắt buộc")
    private LocalDateTime endTime;

    @Min(value = 1, message = "Số tín chỉ tối thiểu phải từ 1")
    private Integer minCredits;

    @Max(value = 50, message = "Số tín chỉ tối đa không nên vượt quá 50")
    private Integer maxCredits;

    private Boolean allowRetake = false;
    private Boolean isActive = true;
    private String targetConfig; // JSON String
}
