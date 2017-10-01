package com.lginterview.providers;

import com.lginterview.common.CourseFunction;
import com.lginterview.dto.CourseRequest;
import com.lginterview.dto.CourseStatus;
import com.lginterview.dto.RequestType;
import com.lginterview.entities.Course;
import com.lginterview.entities.User;
import com.lginterview.exceptions.CourseDataRetrievalException;
import com.lginterview.repositories.CourseRepository;
import com.lginterview.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class CourseProviderImpl implements CourseProvider {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final Map<RequestType, CourseFunction<CourseRequest, Long>> courseActions = new HashMap<>();

    @Autowired
    public CourseProviderImpl(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;

        this.courseActions.put(RequestType.INIT, (this::createCourse));
        this.courseActions.put(RequestType.SAVE, (this::updateCourse));
        this.courseActions.put(RequestType.FINISH, (this::closeCourse));
    }

    @Override
    @Transactional
    public Long modifyCourse(CourseRequest courseRequest) throws CourseDataRetrievalException {
        if (courseRequest == null)
            throw new IllegalArgumentException("Course Request cannot be null");

        if (courseRequest.getType() == null)
            throw new IllegalArgumentException("Course Request Type cannot be null");

        if (courseActions.containsKey(courseRequest.getType()))
            return courseActions.get(courseRequest.getType()).apply(courseRequest);
        else
            throw new IllegalArgumentException("Invalid Course Request type");
    }

    private Long createCourse(CourseRequest courseRequest) throws CourseDataRetrievalException {
        Course newCourse = courseRepository.findOne(courseRequest.getCourseId());
        if (newCourse != null)
            return newCourse.getId();

        User courseUser = userRepository.findOne(courseRequest.getUserId());
        if (courseUser == null)
            throw new CourseDataRetrievalException("User not found");

        newCourse = new Course();
        newCourse.setStartDate(courseRequest.getTimestamp());
        newCourse.setCourseUser(courseUser);
        return courseRepository.save(newCourse).getId();
    }

    private Long updateCourse(CourseRequest courseRequest) throws CourseDataRetrievalException {

        Course courseToUpdate = courseRepository.findOne(courseRequest.getCourseId());
        if (courseToUpdate == null)
            throw new CourseDataRetrievalException("Course Not Found");

        if (courseRequest.getScore() != null) {
            int scoreValue = courseRequest.getScore();
            courseToUpdate.getScores().add(scoreValue);
            courseRepository.save(courseToUpdate);
        }
        return courseToUpdate.getId();
    }

    private Long closeCourse(CourseRequest courseRequest) throws CourseDataRetrievalException {
        Course courseToClose = courseRepository.findOne(courseRequest.getCourseId());
        if (courseToClose == null)
            throw new CourseDataRetrievalException("Course Not Found");

        courseToClose.setEndDate(courseRequest.getTimestamp());
        courseRepository.save(courseToClose);
        return courseToClose.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseStatus getCourseStatus(long userId, long courseId) throws CourseDataRetrievalException {

        Course courseData = getCourse(userId, courseId);

        int bestScore = 0;
        int averageScore = 0;

        if ((courseData.getScores() != null)) {
            bestScore = courseData.getScores()
                    .stream()
                    .mapToInt(score -> score)
                    .max()
                    .orElse(0);

            averageScore = (int) courseData.getScores()
                    .stream()
                    .mapToInt(score -> score)
                    .average()
                    .orElse(0.0);
        }

        int courseTime = 0;
        if (courseData.getEndDate() != null)
            courseTime = (int) (courseData.getEndDate().toEpochSecond() - courseData.getStartDate().toEpochSecond());

        ZonedDateTime utcStartTime=getUTCDateTime(courseData.getStartDate());
        ZonedDateTime utcEndTime=getUTCDateTime(courseData.getEndDate());
        return new CourseStatus(utcStartTime, courseTime,utcEndTime, bestScore, averageScore);
    }

    private Course getCourse(long userId, long courseId) throws CourseDataRetrievalException {
        Optional<Course> matchingCourse = courseRepository.getCourse(userId, courseId);

        if (!matchingCourse.isPresent())
            throw new CourseDataRetrievalException("Course Not Found");

        return matchingCourse.get();
    }

    private ZonedDateTime getUTCDateTime(ZonedDateTime zonedDateTime)
    {
        if(zonedDateTime==null)
            return null;
        return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
    }

}
