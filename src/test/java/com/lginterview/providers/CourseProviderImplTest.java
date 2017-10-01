package com.lginterview.providers;

import com.flextrade.jfixture.JFixture;
import com.lginterview.dto.CourseRequest;
import com.lginterview.dto.CourseStatus;
import com.lginterview.dto.RequestType;
import com.lginterview.entities.Course;
import com.lginterview.entities.User;
import com.lginterview.exceptions.CourseDataRetrievalException;
import com.lginterview.repositories.CourseRepository;
import com.lginterview.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class CourseProviderImplTest {

    private final JFixture fixture = new JFixture();
    private CourseRepository courseRepository;
    private UserRepository userRepository;
    private long userId;
    private long courseId;
    private long addedCourseId;
    private ZonedDateTime courseStartTime;
    private List<Integer> courseScore;


    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        courseRepository = mock(CourseRepository.class);
        userRepository = mock(UserRepository.class);
        userId = fixture.create(Long.class);
        courseId = fixture.create(Long.class);
        addedCourseId = fixture.create(Long.class);
        courseStartTime = ZonedDateTime.now();
        courseScore = fixture.collections().createCollection(List.class, Integer.class);
    }

    @Test
    public void Given_ValidNewCourseRequest_When_modifyCourse_Then_CourseIsAdded()
            throws CourseDataRetrievalException {

        //arrange
        User mockUser = new User();
        mockUser.setId(userId);
        given(userRepository.findOne(userId)).willReturn(mockUser);
        given(courseRepository.save(any(Course.class))).will(invocationOnMock -> {
            Course courseArgument = invocationOnMock.getArgumentAt(0, Course.class);
            courseArgument.setId(addedCourseId);
            return courseArgument;
        });

        ArgumentCaptor<Course> courseArgumentCaptor = ArgumentCaptor.forClass(Course.class);

        CourseRequest courseRequest = getCourseRequest(RequestType.INIT, null);
        CourseProviderImpl courseProvider = new CourseProviderImpl(courseRepository, userRepository);

        //act
        long newCourseId = courseProvider.modifyCourse(courseRequest);

        //assert
        assertEquals(addedCourseId, newCourseId);
        verify(courseRepository).save(courseArgumentCaptor.capture());
        assertEquals(mockUser, courseArgumentCaptor.getValue().getCourseUser());
        assertEquals(courseStartTime, courseArgumentCaptor.getValue().getStartDate());
    }


    @Test(expected = CourseDataRetrievalException.class)
    public void Given_InValidNewCourseRequest_When_modifyCourse_Then_ExceptionIsThrown()
            throws CourseDataRetrievalException {

        //arrange
        given(userRepository.findOne(userId)).willReturn(null);

        CourseRequest courseRequest = getCourseRequest(RequestType.INIT, null);
        CourseProviderImpl courseProvider = new CourseProviderImpl(courseRepository, userRepository);

        //act
        courseProvider.modifyCourse(courseRequest);
    }


    @Test
    public void Given_ValidCourseSaveRequest_When_modifyCourse_Then_CourseIsAdded()
            throws CourseDataRetrievalException {
        //arrange
        int courseScore = fixture.create(Integer.class);

        Course courseToUpdate = new Course();
        courseToUpdate.setId(courseId);
        given(courseRepository.findOne(courseId)).willReturn(courseToUpdate);

        given(courseRepository.save(any(Course.class))).will(invocationOnMock -> {
            Course courseArgument = invocationOnMock.getArgumentAt(0, Course.class);
            courseArgument.setId(addedCourseId);
            return courseArgument;
        });

        ArgumentCaptor<Course> courseArgumentCaptor = ArgumentCaptor.forClass(Course.class);

        CourseRequest courseRequest = getCourseRequest(RequestType.SAVE, courseScore);
        CourseProviderImpl courseProvider = new CourseProviderImpl(courseRepository, userRepository);

        //act
        long newCourseId = courseProvider.modifyCourse(courseRequest);

        //assert
        assertEquals(addedCourseId, newCourseId);
        verify(courseRepository).save(courseArgumentCaptor.capture());

        List<Integer> scoreList = courseArgumentCaptor.getValue().getScores();
        assertEquals(1, scoreList.size());
        assertEquals(courseScore, scoreList.get(0).intValue());
    }

    @Test(expected = CourseDataRetrievalException.class)
    public void Given_InValidCourseSaveRequest_When_modifyCourse_Then_ExceptionIsThrown()
            throws CourseDataRetrievalException {

        //arrange
        int courseScore = fixture.create(Integer.class);

        given(courseRepository.findOne(courseId)).willReturn(null);
        given(courseRepository.save(any(Course.class))).will(invocationOnMock -> {
            Course courseArgument = invocationOnMock.getArgumentAt(0, Course.class);
            courseArgument.setId(addedCourseId);
            return courseArgument;
        });

        CourseRequest courseRequest = getCourseRequest(RequestType.SAVE, courseScore);
        CourseProviderImpl courseProvider = new CourseProviderImpl(courseRepository, userRepository);

        //act
        courseProvider.modifyCourse(courseRequest);
    }

    @Test
    public void Given_ValidCourseFinishRequest_When_modifyCourse_Then_CourseIsAdded()
            throws CourseDataRetrievalException {

        //arrange
        Course courseToClose = new Course();
        courseToClose.setId(courseId);
        given(courseRepository.findOne(courseId)).willReturn(courseToClose);

        given(courseRepository.save(any(Course.class))).will(invocationOnMock -> {
            Course courseArgument = invocationOnMock.getArgumentAt(0, Course.class);
            courseArgument.setId(addedCourseId);
            return courseArgument;
        });

        ArgumentCaptor<Course> courseArgumentCaptor = ArgumentCaptor.forClass(Course.class);

        CourseRequest courseRequest = getCourseRequest(RequestType.FINISH, null);
        CourseProviderImpl courseProvider = new CourseProviderImpl(courseRepository, userRepository);

        //act
        long newCourseId = courseProvider.modifyCourse(courseRequest);

        //assert
        assertEquals(addedCourseId, newCourseId);
        verify(courseRepository).save(courseArgumentCaptor.capture());
        assertEquals(courseStartTime, courseArgumentCaptor.getValue().getEndDate());

    }

    @Test(expected = CourseDataRetrievalException.class)
    public void Given_InValidCourseFinishRequest_When_modifyCourse_Then_ExceptionIsThrown()
            throws CourseDataRetrievalException {

        //arrange
        given(courseRepository.findOne(courseId)).willReturn(null);

        given(courseRepository.save(any(Course.class))).will(invocationOnMock -> {
            Course courseArgument = invocationOnMock.getArgumentAt(0, Course.class);
            courseArgument.setId(addedCourseId);
            return courseArgument;
        });

        CourseRequest courseRequest = getCourseRequest(RequestType.FINISH, null);
        CourseProviderImpl courseProvider = new CourseProviderImpl(courseRepository, userRepository);

        //act
        courseProvider.modifyCourse(courseRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Given_NullCourseRequest_When_modifyCourse_Then_ExceptionIsThrown()
            throws CourseDataRetrievalException {
        //arrange

        CourseProviderImpl courseProvider = new CourseProviderImpl(courseRepository, userRepository);

        //act
        courseProvider.modifyCourse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Given_NullTypedCourseRequest_When_modifyCourse_Then_ExceptionIsThrown()
            throws CourseDataRetrievalException {

        //arrange
        CourseRepository courseRepository = mock(CourseRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        CourseProviderImpl courseProvider = new CourseProviderImpl(courseRepository, userRepository);

        CourseRequest courseRequest =
                getCourseRequest(null, null);

        //act
        courseProvider.modifyCourse(courseRequest);
    }

    @Test
    public void Given_StartedCourseUserAndCourseId_When_getCourseStatus_Then_StartedCourseStatusIsReturned()
            throws CourseDataRetrievalException {

        //arrange
        Course startedCourse = getCourse();
        given(courseRepository.getCourse(userId, courseId)).willReturn(Optional.of(startedCourse));

        CourseProviderImpl courseProvider = new CourseProviderImpl(courseRepository, userRepository);

        CourseStatus expectedCourseStatus =
                new CourseStatus(courseStartTime.withZoneSameInstant(ZoneId.of("UTC")),
                        0,
                        null,
                        0,
                        0);

        //act
        CourseStatus courseStatus = courseProvider.getCourseStatus(userId, courseId);

        //assert
        assertNotNull(courseStatus);
        assertEquals(expectedCourseStatus, courseStatus);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Given_SavedCourseUserAndCourseId_When_getCourseStatus_Then_SavedCourseStatusIsReturned()
            throws CourseDataRetrievalException {
        //arrange
        int expectedBestScore = courseScore.stream().mapToInt(score -> score).max().orElse(0);
        int expectedAverageScore = (int) courseScore.stream().mapToInt(score -> score).average().orElse(0.0);

        Course savedCourse = getCourse();
        savedCourse.getScores().addAll(courseScore);

        given(courseRepository.getCourse(userId, courseId)).willReturn(Optional.of(savedCourse));

        CourseStatus expectedCourseStatus =
                new CourseStatus(courseStartTime.withZoneSameInstant(ZoneId.of("UTC")),
                        0,
                        null,
                        expectedBestScore,
                        expectedAverageScore);

        CourseProviderImpl courseProvider = new CourseProviderImpl(courseRepository, userRepository);

        //act
        CourseStatus courseStatus = courseProvider.getCourseStatus(userId, courseId);

        //assert
        assertNotNull(courseStatus);
        assertEquals(expectedCourseStatus, courseStatus);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Given_FinishedCourseUserAndCourseId_When_getCourseStatus_Then_FinishedCourseStatusIsReturned()
            throws CourseDataRetrievalException {

        //arrange
        ZonedDateTime courseEndTime = courseStartTime.plusMinutes(4);
        Course finishedCourse = getCourse();
        finishedCourse.getScores().addAll(courseScore);
        finishedCourse.setEndDate(courseEndTime);
        given(courseRepository.getCourse(userId, courseId)).willReturn(Optional.of(finishedCourse));

        CourseStatus expectedCourseStatus = getCourseStatus(courseEndTime);
        CourseProviderImpl courseProvider = new CourseProviderImpl(courseRepository, userRepository);

        //act
        CourseStatus courseStatus = courseProvider.getCourseStatus(userId, courseId);

        //assert
        assertNotNull(courseStatus);
        assertEquals(expectedCourseStatus, courseStatus);
    }

    @Test(expected = CourseDataRetrievalException.class)
    public void Given_InvalidCourseUserAndCourseId_When_getCourseStatus_Then_ExceptionIsThrown()
            throws CourseDataRetrievalException {

        //arrange
        given(courseRepository.getCourse(userId, courseId)).willReturn(Optional.empty());

        CourseProviderImpl courseProvider = new CourseProviderImpl(courseRepository, userRepository);

        //act
        courseProvider.getCourseStatus(userId, courseId);
    }

    private CourseStatus getCourseStatus(ZonedDateTime courseEndTime) {
        int expectedBestScore = courseScore
                .stream()
                .mapToInt(score -> score)
                .max()
                .orElse(0);

        int expectedAverageScore = (int) courseScore
                .stream()
                .mapToInt(score -> score)
                .average()
                .orElse(0.0);

        int expectedCourseTime = (int) (courseEndTime.toEpochSecond() - courseStartTime.toEpochSecond());

        return new CourseStatus(courseStartTime.withZoneSameInstant(ZoneId.of("UTC")),
                expectedCourseTime,
                courseEndTime.withZoneSameInstant(ZoneId.of("UTC")),
                expectedBestScore,
                expectedAverageScore);
    }

    private CourseRequest getCourseRequest(RequestType requestType, Integer score) {
        return new CourseRequest(
                userId,
                courseId,
                requestType,
                score,
                courseStartTime);
    }

    private Course getCourse() {
        Course finishedCourse = new Course();
        finishedCourse.setId(courseId);
        finishedCourse.setStartDate(courseStartTime);
        return finishedCourse;
    }
}