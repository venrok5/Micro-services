package com.example.user_service_API.service;

import java.util.List;

import com.example.user_service_API.dto.UserDto;

public interface UserService {
    UserDto create(UserDto userDto);
    UserDto getUserById(Long id);
    List<UserDto> getAll();
    UserDto update(Long id, UserDto userDto);
    void delete(Long id);
}