package com.example.user_service_API;

import com.example.user_service_API.controller.UserController;
import com.example.user_service_API.dto.UserDto;
import com.example.user_service_API.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_returnsCreatedUser() throws Exception {
    	
        UserDto inputUser = new UserDto(null, "name1", "name1@gmail.com", 25);
        UserDto answer = new UserDto(1L, "name1", "name1@gmail.com", 25);

        Mockito.when(service.create(any(UserDto.class) )).thenReturn(answer); // всегда вернет answer

        mockMvc.perform(post("/api/users") // запрос
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputUser) ))
                .andExpect(status().isCreated() ) // 201 Created
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name1"))
                .andExpect(jsonPath("$.email").value("name1@gmail.com"))
                .andExpect(jsonPath("$.age").value(25) );
    }

    @Test
    void getUser_returnsUserById() throws Exception {
        UserDto user = new UserDto(1L, "name2", "name2@gmail.com", 30);
        
        Mockito.when(service.getUserById(1L) ).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.name").value("name2"))
                .andExpect(jsonPath("$.email").value("name2@gmail.com"))
                .andExpect(jsonPath("$.age").value(30) );
    }

    @Test
    void getAllUsers_returnsListOfUsers() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "x", "x@gmail.com", 22),
                new UserDto(2L, "y", "y@gmail.com", 23)
        );

        Mockito.when(service.getAll() ).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateUser_returnsUpdatedUser() throws Exception {
        UserDto updatedUser = new UserDto(1L, "Updated", "upd@example.com", 35);
        
        Mockito.when(service.update(eq(1L), any(UserDto.class) )).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.age").value(35))
                .andExpect(jsonPath("$.email").value("upd@example.com"));
    }

    @Test
    void deleteUser_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent()); // 204

        Mockito.verify(service).delete(1L);
    }
}