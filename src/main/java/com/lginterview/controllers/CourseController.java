package com.lginterview.controllers;

import com.lginterview.dto.CourseRequest;
import com.lginterview.dto.CourseStatus;
import com.lginterview.exceptions.CourseDataRetrievalException;
import com.lginterview.providers.CourseProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class CourseController {

    private final CourseProvider courseProvider;

    @Autowired
    public CourseController(CourseProvider courseProvider) {
        this.courseProvider = courseProvider;
    }

    @RequestMapping(path = "/status/course", method = RequestMethod.POST)
    public ResponseEntity<Long> modifyCourse(@RequestBody @Validated CourseRequest courseRequest)
            throws CourseDataRetrievalException {
        return new ResponseEntity<>(courseProvider.modifyCourse(courseRequest), HttpStatus.OK);
    }

    @RequestMapping(path = "/session/user/{userId}/course/{coursesId}/state", method = RequestMethod.GET)
    public ResponseEntity<CourseStatus> getCourseStatus(@PathVariable("userId" ) long userId,
                                                        @PathVariable("coursesId") long coursesId)
            throws CourseDataRetrievalException {
        CourseStatus courseStatus = courseProvider.getCourseStatus(userId, coursesId);
        return new ResponseEntity<>(courseStatus, HttpStatus.OK);
    }
}
