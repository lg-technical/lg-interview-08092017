package com.lginterview.filter;

import com.google.common.util.concurrent.RateLimiter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ThrottlingFilter implements Filter {

    private final String limitationPath;
    private final RateLimiter rateLimiter;
    private final String limitMessage;

    @Autowired
    public ThrottlingFilter(@Value("${courseservice.limitation-path}") String limitationPath,
                            @Value("${courseservice.limitation-count}") int limitationCount,
                            @Value("${courseservice.limitation-message}") String limitMessage) {
        this.limitationPath = limitationPath;
        this.rateLimiter = RateLimiter.create(limitationCount);
        this.limitMessage = limitMessage;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        if (!(servletRequest instanceof HttpServletRequest) ||
                !(servletResponse instanceof HttpServletResponse)) {
            throw new ServletException("Only http requests are supported ");
        }

        internalDoFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void internalDoFilter(HttpServletRequest servletRequest,
                                  HttpServletResponse servletResponse,
                                  FilterChain filterChain)
            throws IOException, ServletException {

        if (servletRequest.getRequestURI().contains(limitationPath)) {
            if (!rateLimiter.tryAcquire()) {
                servletResponse.reset();
                servletResponse.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, limitMessage);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
