package com.lginterview.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(value = {CourseDataRetrievalException.class})
    public void dataRetrievalException(CourseDataRetrievalException e, HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception se) {
            logger.error("Exception sending error response:", se);
        }
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void validationException(IllegalArgumentException e, HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception se) {
            logger.error("Exception sending error response:", se);
        }
    }

    @ExceptionHandler(value = {Exception.class})
    public void generalException(HttpServletResponse response, Exception e) {
        try {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception se) {
            logger.error("Exception sending error response:", se);
        }
    }
}

