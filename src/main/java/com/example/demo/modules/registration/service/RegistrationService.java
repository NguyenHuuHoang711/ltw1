
package com.example.demo.modules.registration.service;

import com.example.demo.modules.registration.request.CourseRegistrationRequest;
import com.example.demo.modules.registration.response.CourseRegistrationResponse;
import com.example.demo.modules.registration.response.RegistrationPeriodResponse;

import java.util.List;
import java.util.UUID;

public interface RegistrationService {
    List<RegistrationPeriodResponse> getActiveRegistrationPeriods();
    List<CourseRegistrationResponse> getMyRegistrations(UUID studentId);
    CourseRegistrationResponse registerCourse(CourseRegistrationRequest request);
    CourseRegistrationResponse cancelRegistration(UUID registrationId, UUID studentId);
}
