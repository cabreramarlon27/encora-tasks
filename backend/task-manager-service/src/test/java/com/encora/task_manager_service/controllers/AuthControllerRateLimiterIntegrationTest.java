package com.encora.task_manager_service.controllers;

import com.encora.task_manager_service.models.User;
import com.encora.task_manager_service.repositories.UserRepository;
import com.encora.task_manager_service.security.AuthenticationRequest;
import com.encora.task_manager_service.services.LoginAttemptService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthControllerRateLimiterIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LoginAttemptService loginAttemptService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @AfterEach
    void resetAttemptCache(){
        loginAttemptService.resetAttemptsCache();
        userRepository.deleteAll();
    }


    @BeforeEach
    public void setUp() {
        User user = new User(null, "admin", "admin", "admin@example.com", true, List.of("ROLE_USER"));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
    }

    @Test
    void testLoginWithRateLimiting_withinLimit_shouldSucceed() throws Exception {
        // Perform less than MAX_ATTEMPT requests
        for (int i = 0; i < 4; i++) {
            makeLoginRequest("admin@example.com", "admin")
                    .andExpect(status().isOk()); // Or your expected successful status
        }
    }

    @Test
    void testLoginWithRateLimiting_exceedsLimit_shouldBeBlocked() throws Exception {
        // Perform MAX_ATTEMPT requests
        for (int i = 0; i < 5; i++) {
            makeLoginRequest("admin1@example.com", "admin123"); // Status doesn't matter here
        }
        // The next request should be blocked
        makeLoginRequest("admin@example.com", "admin")
                .andExpect(status().isTooManyRequests());
    }

    private ResultActions makeLoginRequest(String email, String password) throws Exception {
        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AuthenticationRequest(email, password))));
    }
}
