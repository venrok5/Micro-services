package com.example.user_service_API.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.user_service_API.dto.NotificationMessage;
import com.example.user_service_API.dto.UserDto;
import com.example.user_service_API.entity.User;
import com.example.user_service_API.mapper.UserMapper;
import com.example.user_service_API.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    
    private final KafkaProducer kafkaProducer;

    public UserDto create(UserDto dto) {
        User user = UserMapper.toEntity(dto);
        UserDto savedUser = UserMapper.toDto(userRepository.save(user) );
        
        // оповещание в кафку
        NotificationMessage message = new NotificationMessage(savedUser.getEmail(), "CREATED");
        kafkaProducer.sendNotification(message);
        
        return savedUser;
    }

    public UserDto getUserById(Long id) {
        User user = getUser(id);
        
        return UserMapper.toDto(user);
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(user -> UserMapper.toDto(user))
                .collect(Collectors.toList() );
    }

    public UserDto update(Long id, UserDto dto) {
    	User user = getUser(id);
        
        user.setName(dto.getName() );
        user.setEmail(dto.getEmail() );
        user.setAge(dto.getAge() );
        
        return UserMapper.toDto(userRepository.save(user) );
    }

    public void delete(Long id) {
    	Optional<User> userOpt = userRepository.findById(id);
        
    	if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            userRepository.deleteById(id);
            
            NotificationMessage message = new NotificationMessage(user.getEmail(), "DELETED");
            kafkaProducer.sendNotification(message);
        } else {
            throw new EntityNotFoundException("User with ID " + id + " not found.");
        }
    }
    
    private User getUser(Long id) {
    	return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found") );
    }
}