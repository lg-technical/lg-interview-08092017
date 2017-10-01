package com.lginterview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public class CourseRequest {

    private final long userId;
    private final long courseId;
    private final RequestType type;
    private final Integer score;
    private final ZonedDateTime timestamp;

    public CourseRequest(@JsonProperty(value = "userId", required = true) long userId,
                         @JsonProperty(value = "courseId", required = true) long courseId,
                         @JsonProperty(value = "type", required = true) RequestType type,
                         @JsonProperty("score") Integer score,
                         @JsonProperty(value = "timestamp", required = true) ZonedDateTime timestamp) {

        this.userId = userId;
        this.courseId = courseId;
        this.type = type;
        this.score = score;
        this.timestamp = timestamp;
    }

    @NotNull
    public long getUserId() {
        return userId;
    }

    @NotNull
    public long getCourseId() {
        return courseId;
    }

    @NotNull
    public RequestType getType() {
        return type;
    }

    public Integer getScore() {
        return score;
    }

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd[[ ]['T']HH:mm:ss.SSSSSSxxx")
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
