package com.example.notification_service_app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDto {
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String toEmail;
    
    @NotBlank(message = "Тема не может быть пустой")
    private String subject;
    
    @NotBlank(message = "Сообщение не может быть пустым")
    private String message;
}
