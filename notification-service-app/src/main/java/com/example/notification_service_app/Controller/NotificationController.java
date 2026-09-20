package com.example.notification_service_app.Controller;

import com.example.notification_service_app.dto.EmailRequestDto;
import com.example.notification_service_app.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendEmail(@Valid @RequestBody EmailRequestDto request) {
        log.info("Received manual email request for: {}", request.getToEmail());
        
        try {
            emailService.sendEmail(
                request.getToEmail(),
                request.getSubject(),
                request.getMessage()
            );
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Email sent successfully to " + request.getToEmail());
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (Exception e) {
            log.error("Failed to send manual email", e);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to send email: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}