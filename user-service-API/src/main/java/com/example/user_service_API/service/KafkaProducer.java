package com.example.user_service_API.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.user_service_API.dto.NotificationMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    @Value("${kafka.topic}")
    private String topic;

    public void sendNotification(NotificationMessage message) {
        kafkaTemplate.send(topic, message);
    }
}