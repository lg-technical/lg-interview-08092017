package com.lginterview.entities;

import javax.persistence.*;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue
    private long Id;

    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    @ElementCollection
    @CollectionTable(name = "scores")
    private final List<Integer> scores = new LinkedList<>();

    @ManyToOne(targetEntity = User.class)
    private User courseUser;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public List<Integer>getScores() {
        return scores;
    }

    public User getCourseUser() {
        return courseUser;
    }

    public void setCourseUser(User courseUser) {
        this.courseUser = courseUser;
    }

}
