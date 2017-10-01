package com.lginterview.integration;

import com.lginterview.entities.Course;
import com.lginterview.entities.User;
import com.lginterview.repositories.CourseRepository;
import com.lginterview.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CourseRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void Given_ExistingCourse_When_getCourse_Then_CourseIsReturned() {
        //arrange
        User user = userRepository.findOne(1L);
        Course course = new Course();
        course.setCourseUser(user);
        long newCourseId = entityManager.persist(course).getId();

        //act
        Optional<Course> foundCourse = courseRepository.getCourse(user.getId(), newCourseId);

        //assert
        assertTrue(foundCourse.isPresent());
        assertEquals(user, foundCourse.get().getCourseUser());
        assertEquals(newCourseId, foundCourse.get().getId());
    }

    @Test
    public void Given_NotExistingCourse_When_getCourse_Then_EmptyOptionalIsReturned() {
        //arrange
        User user = userRepository.findOne(1L);
        //act
        Optional<Course> foundCourse = courseRepository.getCourse(user.getId(), 1L);

        //assert
        assertFalse(foundCourse.isPresent());
    }


}
