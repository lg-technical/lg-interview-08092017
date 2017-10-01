package com.lginterview.repositories;

import com.lginterview.entities.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Long> {

    @Query(value = "SELECT c from Course c where c.courseUser.Id=:userId and c.Id=:courseId")
    Optional<Course> getCourse(@Param("userId") long userId, @Param("courseId") long courseId);

}
