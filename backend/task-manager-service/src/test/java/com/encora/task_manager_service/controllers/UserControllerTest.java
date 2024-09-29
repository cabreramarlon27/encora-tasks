package com.encora.task_manager_service.controllers;

import com.encora.task_manager_service.dto.UpdateNotificationRequest;
import com.encora.task_manager_service.models.User;
import com.encora.task_manager_service.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setEmail("test@example.com");
        testUser.setNotificationsEnabled(false);
        userRepository.save(testUser);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "test@example.com")
    public void testUpdateUserNotificationsEnabled_Success() throws Exception {
        // Prepare request
        UpdateNotificationRequest request = new UpdateNotificationRequest();
        request.setNotificationsEnabled(true);
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform PATCH request
        ResultActions resultActions = mockMvc.perform(patch("/api/users/me/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // Verify response
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.notificationsEnabled").value(true));
    }

    @Test
    @WithMockUser(username = "test-wrong@example.com")
    public void testUpdateUserNotificationsEnabled_UserNotFound() throws Exception {
        // Prepare request
        UpdateNotificationRequest request = new UpdateNotificationRequest();
        request.setNotificationsEnabled(true);
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform PATCH request with a non-existent userId
        ResultActions resultActions = mockMvc.perform(patch("/api/users/me/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // Verify response
        resultActions.andExpect(status().isNotFound()); // Should return 403 Forbidden
    }

    @Test
    public void testUpdateUserNotificationsEnabled_Unauthorized() throws Exception {
        // Prepare request
        UpdateNotificationRequest request = new UpdateNotificationRequest();
        request.setNotificationsEnabled(true);
        String requestJson = objectMapper.writeValueAsString(request);

        // Perform PATCH request without authentication
        ResultActions resultActions = mockMvc.perform(patch("/api/users/me/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));

        // Verify response
        resultActions.andExpect(status().isUnauthorized());
    }
}
