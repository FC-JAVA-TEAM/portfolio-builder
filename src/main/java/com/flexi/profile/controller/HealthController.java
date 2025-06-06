package com.flexi.profile.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flexi.profile.util.LogUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        LogUtil.logMethodEntry(logger, "ping");
        try {
            String response = "pong";
            LogUtil.logMethodExit(logger, "ping", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogUtil.logError(logger, "Error in health check", e);
            throw e;
        }
    }
    
  
}
