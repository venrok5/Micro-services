package com.example.notification_service_app;

import com.example.notification_service_app.dto.NotificationEventDto;
import com.example.notification_service_app.service.EmailService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
class NotificationServiceIT {
	
	@Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
        );

	 @Autowired
	 private KafkaTemplate<String, Object> kafkaTemplate;
	
	 @MockitoBean
	 private EmailService emailService;
	
	 @DynamicPropertySource
	 static void properties(DynamicPropertyRegistry registry) {
		 registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
		 registry.add("spring.kafka.producer.value-serializer", 
				 () -> JsonSerializer.class.getName() );
		 registry.add("spring.kafka.producer.key-serializer",
				 () -> "org.apache.kafka.common.serialization.StringSerializer");
		 registry.add("spring.kafka.consumer.value-deserializer",
				 () -> "org.springframework.kafka.support.serializer.JsonDeserializer");
		 registry.add("spring.kafka.consumer.key-deserializer",
				 () -> "org.apache.kafka.common.serialization.StringDeserializer");
		 registry.add("spring.kafka.consumer.properties.spring.json.trusted.packages",
				 () -> "com.example.notification_service_app.dto");
		 registry.add("spring.kafka.consumer.properties.spring.json.value.default.type",
				 () -> "com.example.notification_service_app.dto.NotificationEventDto");
		 registry.add("spring.kafka.consumer.properties.spring.json.use.type.headers",
				 () -> "true");
		 registry.add("spring.kafka.consumer.auto-offset-reset",
				 () -> "earliest");
	}
	

	
	@Test
	void shouldSendEmailWhenUserCreated() {
	
	    NotificationEventDto event = new NotificationEventDto();
	    event.setEventType("CREATED");
	    event.setToEmail("test@example.com");
	    event.setTimestamp(LocalDateTime.now());
	     
	    Map<String, Object> additionalData = new HashMap<>();
	    additionalData.put("userId", 1L);
	    additionalData.put("userName", "Test User");
	    event.setAdditionalData(additionalData);
	
	    kafkaTemplate.send("bank-notifications", event);
	     
	    verify(emailService, timeout(5000).times(1)).sendEmail(
	        "test@example.com",
	        "Аккаунт создан",
	        "Здравствуйте! Ваш аккаунт на сайте был успешно создан."
	    );
	}
	
	@Test
	void shouldSendEmailWhenUserDeleted() {
	     
	    NotificationEventDto event = new NotificationEventDto();
	    event.setEventType("DELETED");
	    event.setToEmail("test@example.com");
	    event.setTimestamp(LocalDateTime.now());
	     
	    kafkaTemplate.send("bank-notifications", event);
	    
	    verify(emailService, timeout(5000).times(1)).sendEmail(
	         "test@example.com",
	         "Аккаунт удалён",
	         "Здравствуйте! Ваш аккаунт был удалён."
	     );
	 }
	
	@Test
	void shouldSendEmailWithCustomSubjectAndMessage() {
	     
	    NotificationEventDto event = new NotificationEventDto();
	    event.setEventType("CUSTOM");
	    event.setToEmail("test@example.com");
	    event.setSubject("Персональное уведомление");
	    event.setMessage("Это персональное сообщение для пользователя");
	    event.setTimestamp(LocalDateTime.now());
	     
	    kafkaTemplate.send("bank-notifications", event);
	     
	    verify(emailService, timeout(5000).times(1)).sendEmail(
	        "test@example.com",
	        "Персональное уведомление",
	        "Это персональное сообщение для пользователя"
	   );
	}
	
	@Test
	void shouldHandleMultipleEvents() {
	     
		NotificationEventDto created = new NotificationEventDto();
	    created.setEventType("CREATED");
	    created.setToEmail("test1@example.com");
	    created.setTimestamp(LocalDateTime.now());
	     
	    NotificationEventDto deleted = new NotificationEventDto();
	    deleted.setEventType("DELETED");
	    deleted.setToEmail("test2@example.com");
	    deleted.setTimestamp(LocalDateTime.now());
	     
	    kafkaTemplate.send("bank-notifications", created);
	    kafkaTemplate.send("bank-notifications", deleted);
	     
	    verify(emailService, timeout(5000).times(1)).sendEmail(
	        "test1@example.com",
	        "Аккаунт создан",
	        "Здравствуйте! Ваш аккаунт на сайте был успешно создан."
	    );
	     
	    verify(emailService, timeout(5000).times(1)).sendEmail(
	        "test2@example.com",
	        "Аккаунт удалён",
	        "Здравствуйте! Ваш аккаунт был удалён.");
	}
}