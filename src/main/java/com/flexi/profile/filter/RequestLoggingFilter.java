package com.flexi.profile.filter;

import com.flexi.profile.util.LogUtil;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
