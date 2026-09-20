package com.example.user_service_API.mapper;

import com.example.user_service_API.dto.UserDto;
import com.example.user_service_API.entity.User;

public class UserMapper {
	
    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        
        return dto;
    }

    public static User toEntity(UserDto dto) {
        User user = new User();
        
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        
        return user;
    }
}