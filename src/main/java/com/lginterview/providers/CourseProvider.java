package com.lginterview.providers;

import com.lginterview.dto.CourseRequest;
import com.lginterview.dto.CourseStatus;
import com.lginterview.exceptions.CourseDataRetrievalException;

public interface CourseProvider {
    Long modifyCourse(CourseRequest courseRequest) throws CourseDataRetrievalException;

    CourseStatus getCourseStatus(long userId, long courseId) throws CourseDataRetrievalException;
}
