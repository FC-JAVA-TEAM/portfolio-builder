package com.flexi.profile.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.flexi.profile.util.LogUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            LogUtil.setupMDC();
            String path = request.getRequestURI();
            String method = request.getMethod();
            
            logger.info("Received {} request to {}", method, path);
            LogUtil.setMethod(method + " " + path);

            long startTime = System.currentTimeMillis();
            filterChain.doFilter(request, response);
            long duration = System.currentTimeMillis() - startTime;

            logger.info("Completed {} {} with status {} in {} ms",
                    method, path, response.getStatus(), duration);
        } catch (Exception e) {
            LogUtil.logError(logger, "Request processing failed", e);
            throw e;
        } finally {
            LogUtil.clearMDC();
        }
    }
}
