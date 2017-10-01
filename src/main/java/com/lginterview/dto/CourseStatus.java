package com.lginterview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class CourseStatus {

    private final ZonedDateTime startedDate;
    private final int totalSessionTime;
    private final ZonedDateTime endDate;
    private final int bestScore;
    private final int averageScore;

    public CourseStatus(@JsonProperty(value = "started_date", required = true)
                        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSSxxx")
                                ZonedDateTime startedDate,
                        @JsonProperty("total_session_time") int totalSessionTime,
                        @JsonProperty("end_date")
                        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSSxxx")
                                ZonedDateTime endDate,
                        @JsonProperty("best_score") int bestScore,
                        @JsonProperty("average_score") int averageScore) {

        this.startedDate = startedDate;
        this.totalSessionTime = totalSessionTime;
        this.endDate = endDate;
        this.bestScore = bestScore;
        this.averageScore = averageScore;
    }

    @JsonProperty("started_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSSxxx")
    public ZonedDateTime getStartedDate() {
        return startedDate;
    }

    @JsonProperty("total_session_time")
    public int getTotalSessionTime() {
        return totalSessionTime;
    }

    @JsonProperty("end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSSxxx")
    public ZonedDateTime getEndDate() {
        return endDate;
    }

    @JsonProperty("best_score")
    public int getBestScore() {
        return bestScore;
    }

    @JsonProperty("average_score")
    public int getAverageScore() {
        return averageScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseStatus that = (CourseStatus) o;

        if (totalSessionTime != that.totalSessionTime) return false;
        if (bestScore != that.bestScore) return false;
        if (averageScore != that.averageScore) return false;
        if (startedDate != null ? !startedDate.equals(that.startedDate) : that.startedDate != null) return false;
        return endDate != null ? endDate.equals(that.endDate) : that.endDate == null;
    }

    @Override
    public int hashCode() {
        int result = startedDate != null ? startedDate.hashCode() : 0;
        result = 31 * result + totalSessionTime;
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + bestScore;
        result = 31 * result + averageScore;
        return result;
    }
}
