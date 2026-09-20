package com.example.notification_service_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.example.notification_service_app.dto.NotificationEventDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "bank-notifications", groupId = "notification-group")
    public void consumeNotification(NotificationEventDto event) {
        
    	log.info("Received notification: {}", event.getEventType());
        
        try {
        	
        	String subject = (event.getSubject() != null && !event.getSubject().isEmpty()) ? event.getSubject() : buildSubject(event.getEventType());
            String message = (event.getMessage() != null && !event.getMessage().isEmpty()) ? event.getMessage() : buildMessage(event.getEventType());
        	
            emailService.sendEmail(
                    event.getToEmail(),
                    subject,
                    message
                );
            
            log.info("Email sent to: {}", event.getToEmail());
        } catch (Exception e) {
            
        	log.error("Failed to send email to: {}", event.getToEmail(), e);
        }
    }
    
    private String buildMessage(String eventType) {
        if ("CREATED".equals(eventType)) {
            return "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
        
        } else if ("DELETED".equals(eventType)) {
            return "Здравствуйте! Ваш аккаунт был удалён.";
        }
        return "Изменение статуса аккаунта.";
    }
    
    private String buildSubject(String eventType) {
        if ("CREATED".equals(eventType)) {
            return "Аккаунт создан";
        
        } else if ("DELETED".equals(eventType)) {
            return "Аккаунт удалён";
        }
        
        return "Уведомление";
    }
    
}