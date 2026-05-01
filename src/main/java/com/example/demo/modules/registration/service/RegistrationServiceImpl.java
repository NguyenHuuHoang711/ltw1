package com.example.demo.modules.registration.service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.modules.registration.entity.Course;
import com.example.demo.modules.registration.entity.CourseRegistration;
import com.example.demo.modules.registration.entity.RegistrationPeriod;
import com.example.demo.modules.registration.mapper.CourseRegistrationMapper;
import com.example.demo.modules.registration.mapper.RegistrationPeriodMapper;
import com.example.demo.modules.registration.repository.CourseRepository;
import com.example.demo.modules.registration.repository.CourseRegistrationRepository;
import com.example.demo.modules.registration.repository.EquivalentCourseRepository;
import com.example.demo.modules.registration.repository.RegistrationPeriodRepository;
import com.example.demo.modules.registration.request.CourseRegistrationRequest;
import com.example.demo.modules.registration.response.CourseRegistrationResponse;
import com.example.demo.modules.registration.response.RegistrationPeriodResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationPeriodRepository registrationPeriodRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final EquivalentCourseRepository equivalentCourseRepository;
    private final CourseRepository courseRepository;
    private final RegistrationPeriodMapper registrationPeriodMapper;
    private final CourseRegistrationMapper courseRegistrationMapper;

    @Override
    public List<RegistrationPeriodResponse> getActiveRegistrationPeriods() {
        // Changed findByIsActiveTrueAndIsOpenTrue to findByIsActiveTrue
        // since isOpen doesn't exist in the new RegistrationPeriod entity
        return registrationPeriodRepository.findByIsActiveTrue().stream()
                .map(registrationPeriodMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseRegistrationResponse> getMyRegistrations(UUID studentId) {
        return courseRegistrationRepository.findByStudentId(studentId).stream()
                .map(courseRegistrationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseRegistrationResponse registerCourse(CourseRegistrationRequest request) {
        RegistrationPeriod period = validateRegistrationPeriod(request.getRegistrationPeriodId(), request.isForce());
        
        // Pass request.getCourseId() which will be checked against courseClassId in the DB
        validateDuplicateRegistration(request.getStudentId(), request.getCourseId(), request.getRegistrationPeriodId());

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException("Course not found"));

        if (!request.isForce()) {
            validateEquivalentCourses(request.getStudentId(), course.getId(), request.getRegistrationPeriodId());
            validateCredits(request.getStudentId(), course.getCredits(), period.getMaxCredits(), request.getRegistrationPeriodId());
        }

        CourseRegistration registration = courseRegistrationMapper.toEntity(request);
        CourseRegistration savedRegistration = courseRegistrationRepository.save(registration);

        return courseRegistrationMapper.toResponse(savedRegistration);
    }

    @Override
    @Transactional
    public CourseRegistrationResponse cancelRegistration(UUID registrationId, UUID studentId) {
        CourseRegistration registration = courseRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new NotFoundException("Registration not found"));

        if (!registration.getStudentId().equals(studentId)) {
            throw new BadRequestException("You are not authorized to cancel this registration");
        }

        if (registration.getIsPaid()) {
            throw new BadRequestException("Cannot cancel a paid registration");
        }

        registration.setStatus(3); // 3=cancel
        CourseRegistration updatedRegistration = courseRegistrationRepository.save(registration);
        return courseRegistrationMapper.toResponse(updatedRegistration);
    }

    private RegistrationPeriod validateRegistrationPeriod(UUID periodId, boolean force) {
        RegistrationPeriod period = registrationPeriodRepository.findById(periodId)
                .orElseThrow(() -> new NotFoundException("Registration period not found"));

        if (force) return period;

        // Removed check for period.getIsOpen() since it doesn't exist in the entity
        if (!period.getIsActive()) {
            throw new BadRequestException("Registration period is not active");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(period.getStartTime()) || now.isAfter(period.getEndTime())) {
            throw new BadRequestException("Registration is not within the allowed time frame");
        }
        return period;
    }

    private void validateDuplicateRegistration(UUID studentId, UUID courseId, UUID registrationPeriodId) {
        // Calls the method that maps courseId to the courseClassId field
        courseRegistrationRepository.findByStudentIdAndCourseClassIdAndRegistrationPeriodId(studentId, courseId, registrationPeriodId)
                .ifPresent(r -> {
                    throw new ConflictException("You have already registered for this course");
                });
    }

    private void validateEquivalentCourses(UUID studentId, UUID newCourseId, UUID registrationPeriodId) {
        List<UUID> registeredCourseIds = getRegisteredCourseIds(studentId, registrationPeriodId);
        List<UUID> equivalentCourses = equivalentCourseRepository.findReplacedCourses(newCourseId);
        equivalentCourses.addAll(equivalentCourseRepository.findReplacingCourses(newCourseId));

        for (UUID registeredCourseId : registeredCourseIds) {
            if (equivalentCourses.contains(registeredCourseId)) {
                throw new ConflictException("You cannot register for an equivalent course that replaces a course you are already registered for.");
            }
        }
    }

    private void validateCredits(UUID studentId, BigDecimal newCourseCredits, int maxCredits, UUID registrationPeriodId) {
        BigDecimal currentCredits = getCurrentCredits(studentId, registrationPeriodId);
        if (currentCredits.add(newCourseCredits).compareTo(BigDecimal.valueOf(maxCredits)) > 0) {
            throw new BadRequestException("Exceeds maximum allowed credits (" + maxCredits + ")");
        }
    }

    private List<UUID> getRegisteredCourseIds(UUID studentId, UUID registrationPeriodId) {
        List<CourseRegistration> registrations = courseRegistrationRepository.findByStudentIdAndRegistrationPeriodId(studentId, registrationPeriodId);
        // Extracts the courseClassId from the entity (which we treat as courseId logically)
        return registrations.stream().map(CourseRegistration::getCourseClassId).collect(Collectors.toList());
    }

    private BigDecimal getCurrentCredits(UUID studentId, UUID registrationPeriodId) {
        List<CourseRegistration> registrations = courseRegistrationRepository.findByStudentIdAndRegistrationPeriodId(studentId, registrationPeriodId);
        // Extracts the courseClassId to look up the course
        List<UUID> courseIds = registrations.stream().map(CourseRegistration::getCourseClassId).collect(Collectors.toList());
        List<Course> courses = courseRepository.findAllById(courseIds);
        return courses.stream()
                .map(Course::getCredits)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}