package com.example.demo.modules.registration.service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.modules.registration.entity.Course;
import com.example.demo.modules.registration.entity.CourseRegistration;
import com.example.demo.modules.registration.entity.RegistrationPeriod;
import com.example.demo.modules.registration.mapper.CourseRegistrationMapper;
import com.example.demo.modules.registration.repository.CourseRegistrationRepository;
import com.example.demo.modules.registration.repository.CourseRepository;
import com.example.demo.modules.registration.repository.EquivalentCourseRepository;
import com.example.demo.modules.registration.repository.RegistrationPeriodRepository;
import com.example.demo.modules.registration.request.CourseRegistrationRequest;
import com.example.demo.modules.registration.response.CourseRegistrationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceImplTest {

    @Mock
    private RegistrationPeriodRepository registrationPeriodRepository;
    @Mock
    private CourseRegistrationRepository courseRegistrationRepository;
    @Mock
    private EquivalentCourseRepository equivalentCourseRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseRegistrationMapper courseRegistrationMapper;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private UUID studentId;
    private UUID courseId;
    private UUID registrationPeriodId;
    private CourseRegistrationRequest request;
    private RegistrationPeriod activePeriod;
    private Course course;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        registrationPeriodId = UUID.randomUUID();

        request = new CourseRegistrationRequest();
        request.setStudentId(studentId);
        request.setCourseId(courseId);
        request.setRegistrationPeriodId(registrationPeriodId);
        request.setRegistrationType(1);

        activePeriod = new RegistrationPeriod();
        activePeriod.setId(registrationPeriodId);
        activePeriod.setIsActive(true);
        activePeriod.setStartTime(LocalDateTime.now().minusDays(1));
        activePeriod.setEndTime(LocalDateTime.now().plusDays(1));
        activePeriod.setMaxCredits(20);

        course = new Course();
        course.setId(courseId);
        course.setCredits(new BigDecimal("3.0"));
    }

    @Test
    void registerCourse_Success() {
        // Arrange
        when(registrationPeriodRepository.findById(registrationPeriodId)).thenReturn(Optional.of(activePeriod));
        when(courseRegistrationRepository.findByStudentIdAndCourseClassIdAndRegistrationPeriodId(studentId, courseId, registrationPeriodId))
                .thenReturn(Optional.empty());
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        
        // Mock current credits and equivalent courses
        when(courseRegistrationRepository.findByStudentIdAndRegistrationPeriodId(studentId, registrationPeriodId))
                .thenReturn(Collections.emptyList());
        when(equivalentCourseRepository.findReplacedCourses(courseId)).thenReturn(new java.util.ArrayList<>());
        when(equivalentCourseRepository.findReplacingCourses(courseId)).thenReturn(new java.util.ArrayList<>());

        CourseRegistration entity = new CourseRegistration();
        when(courseRegistrationMapper.toEntity(request)).thenReturn(entity);
        when(courseRegistrationRepository.save(any(CourseRegistration.class))).thenReturn(entity);
        
        CourseRegistrationResponse response = new CourseRegistrationResponse();
        when(courseRegistrationMapper.toResponse(entity)).thenReturn(response);

        // Act
        CourseRegistrationResponse result = registrationService.registerCourse(request);

        // Assert
        assertNotNull(result);
        verify(courseRegistrationRepository, times(1)).save(any(CourseRegistration.class));
    }

    @Test
    void registerCourse_PeriodNotFound_ThrowsNotFoundException() {
        // Arrange
        when(registrationPeriodRepository.findById(registrationPeriodId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> registrationService.registerCourse(request));
    }

    @Test
    void registerCourse_PeriodNotActive_ThrowsBadRequestException() {
        // Arrange
        activePeriod.setIsActive(false);
        when(registrationPeriodRepository.findById(registrationPeriodId)).thenReturn(Optional.of(activePeriod));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> registrationService.registerCourse(request));
    }

    @Test
    void registerCourse_DuplicateRegistration_ThrowsConflictException() {
        // Arrange
        when(registrationPeriodRepository.findById(registrationPeriodId)).thenReturn(Optional.of(activePeriod));
        when(courseRegistrationRepository.findByStudentIdAndCourseClassIdAndRegistrationPeriodId(studentId, courseId, registrationPeriodId))
                .thenReturn(Optional.of(new CourseRegistration()));

        // Act & Assert
        assertThrows(ConflictException.class, () -> registrationService.registerCourse(request));
    }

    @Test
    void registerCourse_MaxCreditsExceeded_ThrowsBadRequestException() {
        // Arrange
        activePeriod.setMaxCredits(5);
        when(registrationPeriodRepository.findById(registrationPeriodId)).thenReturn(Optional.of(activePeriod));
        when(courseRegistrationRepository.findByStudentIdAndCourseClassIdAndRegistrationPeriodId(studentId, courseId, registrationPeriodId))
                .thenReturn(Optional.empty());
        
        Course expensiveCourse = new Course();
        expensiveCourse.setId(courseId);
        expensiveCourse.setCredits(new BigDecimal("6.0")); // Exceeds max 5
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(expensiveCourse));
        
        when(courseRegistrationRepository.findByStudentIdAndRegistrationPeriodId(studentId, registrationPeriodId))
                .thenReturn(Collections.emptyList());
        when(equivalentCourseRepository.findReplacedCourses(courseId)).thenReturn(new java.util.ArrayList<>());
        when(equivalentCourseRepository.findReplacingCourses(courseId)).thenReturn(new java.util.ArrayList<>());

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> registrationService.registerCourse(request));
        assertTrue(exception.getMessage().contains("Exceeds maximum allowed credits"));
    }

    @Test
    void cancelRegistration_Success() {
        // Arrange
        UUID registrationId = UUID.randomUUID();
        CourseRegistration existingRegistration = new CourseRegistration();
        existingRegistration.setId(registrationId);
        existingRegistration.setStudentId(studentId);
        existingRegistration.setIsPaid(false);
        existingRegistration.setStatus(1);

        when(courseRegistrationRepository.findById(registrationId)).thenReturn(Optional.of(existingRegistration));
        when(courseRegistrationRepository.save(any(CourseRegistration.class))).thenReturn(existingRegistration);
        
        CourseRegistrationResponse expectedResponse = new CourseRegistrationResponse();
        when(courseRegistrationMapper.toResponse(existingRegistration)).thenReturn(expectedResponse);

        // Act
        CourseRegistrationResponse result = registrationService.cancelRegistration(registrationId, studentId);

        // Assert
        assertNotNull(result);
        assertEquals(3, existingRegistration.getStatus()); // Status changed to 3 (Cancel)
        verify(courseRegistrationRepository, times(1)).save(existingRegistration);
    }
    
    @Test
    void cancelRegistration_AlreadyPaid_ThrowsBadRequestException() {
        // Arrange
        UUID registrationId = UUID.randomUUID();
        CourseRegistration existingRegistration = new CourseRegistration();
        existingRegistration.setId(registrationId);
        existingRegistration.setStudentId(studentId);
        existingRegistration.setIsPaid(true); // Already paid
        
        when(courseRegistrationRepository.findById(registrationId)).thenReturn(Optional.of(existingRegistration));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> registrationService.cancelRegistration(registrationId, studentId));
    }
}