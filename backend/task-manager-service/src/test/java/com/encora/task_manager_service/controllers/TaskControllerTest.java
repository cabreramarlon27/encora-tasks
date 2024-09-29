package com.encora.task_manager_service.controllers;

import com.encora.task_manager_service.models.Task;
import com.encora.task_manager_service.models.TaskStatus;
import com.encora.task_manager_service.models.User;
import com.encora.task_manager_service.repositories.TaskRepository;
import com.encora.task_manager_service.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;
    private Task task1;
    private Task task2;
    private User user;
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        user = new User(null, "testuser", "testuser", "testuser@email.com", true, List.of("ROLE_USER"));
        user = userRepository.save(user);

        task1 = new Task("1", "Test Task 1", "Description 1", LocalDate.now(), TaskStatus.TODO, user.getId());
        task2 = new Task("2", "Test Task 2", "Description 2", LocalDate.now().plusDays(1), TaskStatus.IN_PROGRESS, "anotheruser");
        taskRepository.save(task1);
        taskRepository.save(task2);
    }

    @AfterEach
    public void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @WithMockUser(username = "testuser@email.com", roles = "USER")
    public void testGetAllTasks_Success() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Test Task 1"));
    }

    @Test
    @WithMockUser(username = "testuser@email.com", roles = "USER")
    public void testGetTaskById_Success() throws Exception {
        mockMvc.perform(get("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task 1"));
    }

    @Test
    @WithMockUser(username = "testuser@email.com", roles = "USER")
    public void testGetTaskById_NotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser@email.com", roles = "USER")
    public void testCreateTask_Success() throws Exception {
        Task newTask = new Task(null, "New Task", "New description", LocalDate.now(), TaskStatus.TODO, "testuser");
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    @WithMockUser(username = "testuser@email.com", roles = "USER")
    public void testUpdateTask_Success() throws Exception {
        Task updatedTask = new Task("1", "Updated Task Title", "Updated description", LocalDate.now(), TaskStatus.IN_PROGRESS, "testuser");
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task Title"));
    }

    @Test
    @WithMockUser(username = "testuser@email.com", roles = "USER")
    public void testUpdateTask_NotFound() throws Exception {
        Task updatedTask = new Task("3", "Updated Task", "Updated Description", LocalDate.now(), TaskStatus.TODO, "testuser");
        mockMvc.perform(put("/api/tasks/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser@email.com", roles = "USER")
    public void testDeleteTask_Success() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(username = "testuser@email.com", roles = "USER")
    public void testDeleteTask_NotFound() throws Exception {
        mockMvc.perform(delete("/api/tasks/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser@email.com", roles = "USER")
    public void testDeleteTask_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/tasks/2"))
                .andExpect(status().isForbidden());
    }
}
