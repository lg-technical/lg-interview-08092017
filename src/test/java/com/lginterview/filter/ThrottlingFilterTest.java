package com.lginterview.filter;

import com.flextrade.jfixture.JFixture;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ThrottlingFilterTest {

    private final JFixture fixture = new JFixture();
    private String limitMessage;
    private String path;
    private ThrottlingFilter throttlingFilter;
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private FilterChain filterChain;

    @Before
    public void setUp() throws Exception {
        limitMessage = fixture.create(String.class);
        path = fixture.create(String.class);
        throttlingFilter = new ThrottlingFilter(path, 1, limitMessage);
        httpServletRequest = mock(HttpServletRequest.class);
        httpServletResponse = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    public void Given_NotMatchingPath_When_doFilter_Then_RequestIsPassed()
            throws IOException, ServletException {
        //arrange
        given(httpServletRequest.getRequestURI()).willReturn(fixture.create(String.class));

        //act
        throttlingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        //assert
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void Given_MatchingPathAndLimitNotReached_When_doFilter_Then_RequestIsPassed()
            throws IOException, ServletException {
        //arrange
        given(httpServletRequest.getRequestURI()).willReturn(path);

        //act
        throttlingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        //assert
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
    }

    @Test
    public void Given_MatchingPathAndLimitReached_When_doFilter_Then_RequestIsBlocked()
            throws IOException, ServletException {
        //arrange
        given(httpServletRequest.getRequestURI()).willReturn(path);

        //act
        throttlingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
        throttlingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        //assert
        verify(filterChain, times(1)).doFilter(httpServletRequest, httpServletResponse);
        verify(httpServletResponse, times(1))
                .sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, limitMessage);
    }

    @Test(expected = ServletException.class)
    public void Given_NonHttpRequest_When_doFilter_Then_ExceptionIsThrown()
            throws IOException, ServletException {
        //arrange
        ServletRequest servletRequest = mock(ServletRequest.class);

        //act
        throttlingFilter.doFilter(servletRequest, httpServletResponse, filterChain);
    }

    @Test(expected = ServletException.class)
    public void Given_NonHttpResponse_When_doFilter_Then_ExceptionIsThrown()
            throws IOException, ServletException {
        //arrange
        ServletResponse servletResponse = mock(ServletResponse.class);

        //act
        throttlingFilter.doFilter(httpServletRequest, servletResponse, filterChain);
    }
}