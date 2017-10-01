package com.lginterview.integration;


import com.flextrade.jfixture.JFixture;
import com.lginterview.CourseServiceApplication;
import com.lginterview.dto.CourseStatus;
import com.lginterview.entities.Course;
import com.lginterview.entities.User;
import com.lginterview.repositories.CourseRepository;
import com.lginterview.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = CourseServiceApplication.class)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CourseApiTests {

    @Autowired
    private TestRestTemplate template;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    private HttpHeaders headers;
    private final JFixture fixture = new JFixture();

    @Before
    public void setUp() throws Exception {
        headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
    }

    @Test
    public void Given_CourseCreateRequest_When_requestCourseModification_Then_CourseIsCreated() {
        //arrange
        String newCourseRequest = "{\"userId\":\"1\",\"courseId\":\"22\",\"type\":\"init\"," +
                "\"timestamp\" :\"2017-06-28 14:21:45.375193+03:00\"}";

        //act
        ResponseEntity<Long> newCourseResponse =
                template.postForEntity(
                        "/status/course",
                        new HttpEntity<>(newCourseRequest, headers),
                        Long.class);

        //assert
        assertNotNull(newCourseResponse);
        assertEquals(HttpStatus.OK, newCourseResponse.getStatusCode());
        assertEquals(1L, newCourseResponse.getBody().longValue());
    }

    @Test
    public void Given_CourseSaveRequestForExistingCourse_When_requestCourseModification_Then_CourseIsUpdated() {
        //arrange
        Course newCourse = new Course();
        long newCourseId = courseRepository.save(newCourse).getId();

        String courseSaveRequest = "{\"userId\":\"1\",\"courseId\":\"" + Long.toString(newCourseId) +
                "\",\"type\":\"save\"," +
                "\"score\":\"45\",\"timestamp\" :\"2017-06-28T14:21:45.375193+03:00\"}";

        //act
        ResponseEntity<Long> courseSaveResponse =
                template.postForEntity(
                        "/status/course",
                        new HttpEntity<>(courseSaveRequest, headers),
                        Long.class);

        //assert
        assertNotNull(courseSaveResponse);
        assertEquals(HttpStatus.OK, courseSaveResponse.getStatusCode());
        assertEquals(newCourseId, courseSaveResponse.getBody().longValue());
    }

    @Test
    public void Given_CourseSaveRequestForNotExistingCourse_When_requestCourseModification_Then_NotFoundIsReturned() {
        //arrange
        String courseSaveRequest = "{\"userId\":\"1\",\"courseId\":\"23\",\"type\":\"save\"," +
                "\"score\":\"45\",\"timestamp\" :\"2017-06-28 14:21:45.375193+03:00\"}";

        //act
        ResponseEntity<String> courseSaveResponse =
                template.postForEntity(
                        "/status/course",
                        new HttpEntity<>(courseSaveRequest, headers),
                        String.class);

        //assert
        assertNotNull(courseSaveResponse);
        assertEquals(HttpStatus.NOT_FOUND, courseSaveResponse.getStatusCode());
    }

    @Test
    public void Given_CourseFinishRequestForExistingCourse_When_requestCourseModification_Then_CourseIsFinished() {
        //arrange
        Course newCourse = new Course();
        long newCourseId = courseRepository.save(newCourse).getId();

        String courseSaveRequest = "{\"userId\":\"1\",\"courseId\":\"" + Long.toString(newCourseId) +
                "\",\"type\":\"finish\"," +
                "\"score\":\"45\",\"timestamp\" :\"2017-06-28T14:21:45.375193+03:00\"}";

        //act
        ResponseEntity<Long> courseFinishResponse =
                template.postForEntity(
                        "/status/course",
                        new HttpEntity<>(courseSaveRequest, headers),
                        Long.class);

        //assert
        assertNotNull(courseFinishResponse);
        assertEquals(HttpStatus.OK, courseFinishResponse.getStatusCode());
        assertEquals(newCourseId, courseFinishResponse.getBody().longValue());
    }

    @Test
    public void Given_CourseFinishRequestForNotExistingCourse_When_requestCourseModification_Then_NotFoundIsReturned() {
        //arrange
        String courseSaveRequest = "{\"userId\":\"1\",\"courseId\":\"23\",\"type\":\"finish\"," +
                "\"score\":\"45\",\"timestamp\" :\"2017-06-28 14:21:45.375193+03:00\"}";

        //act
        ResponseEntity<String> courseFinishResponse =
                template.postForEntity(
                        "/status/course",
                        new HttpEntity<>(courseSaveRequest, headers),
                        String.class);

        //assert
        assertNotNull(courseFinishResponse);
        assertEquals(HttpStatus.NOT_FOUND, courseFinishResponse.getStatusCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Given_CourseStatusRequestForExistingCourse_When_requestCourseStatus_Then_StatusIsReturned() {
        //arrange
        List<Integer> scores = fixture.collections().createCollection(List.class, Integer.class);

        User user = userRepository.findOne(1L);

        Course newCourse = getNewCourse(scores, user);
        long newCourseId = courseRepository.save(newCourse).getId();

        int expectedBestScore = scores.stream().mapToInt(score -> score).max().orElse(0);
        int expectedAverageScore = (int) scores.stream().mapToInt(score -> score).average().orElse(0.0);
        int expectedCourseTime =
                (int) ((newCourse.getEndDate().toEpochSecond() - newCourse.getStartDate().toEpochSecond()));

        CourseStatus expectedCourseStatus =
                new CourseStatus(newCourse.getStartDate().withZoneSameInstant(ZoneId.of("UTC")),
                        expectedCourseTime,
                        newCourse.getEndDate().withZoneSameInstant(ZoneId.of("UTC")),
                        expectedBestScore,
                        expectedAverageScore);

        String courseStatusPath = "/session/user/1/course/" + Long.toString(newCourseId) + "/state";

        //act
        ResponseEntity<CourseStatus> courseStatusResponse =
                template.getForEntity(
                        courseStatusPath,
                        CourseStatus.class);

        //assert
        assertNotNull(courseStatusResponse);
        assertEquals(HttpStatus.OK, courseStatusResponse.getStatusCode());

        CourseStatus returnedCourseStatus = courseStatusResponse.getBody();
        assertEquals(expectedCourseStatus, returnedCourseStatus);
    }

    private Course getNewCourse(List<Integer> scores, User user) {
        Course newCourse = new Course();
        newCourse.setCourseUser(user);
        newCourse.setStartDate(ZonedDateTime.now());
        newCourse.setEndDate(ZonedDateTime.now());
        newCourse.getScores().addAll(scores);
        return newCourse;
    }

    @Test
    public void Given_CourseStatusRequestForNotExistingCourse_When_requestCourseStatus_Then_NotFoundIsReturned() {
        //arrange

        //act
        ResponseEntity<String> courseStatusResponse =
                template.getForEntity(
                        "/session/user/1/course/1/state",
                        String.class);

        //assert
        assertNotNull(courseStatusResponse);
        assertEquals(HttpStatus.NOT_FOUND, courseStatusResponse.getStatusCode());
    }
}
