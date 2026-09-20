package com.example.user_service_API.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.user_service_API.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}