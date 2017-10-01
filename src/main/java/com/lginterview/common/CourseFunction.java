package com.lginterview.common;

import com.lginterview.exceptions.CourseDataRetrievalException;

@FunctionalInterface
public interface CourseFunction<T, R> {
    R apply(T t) throws CourseDataRetrievalException;
}
