//package com.encora.task_manager_service.controllers;
//
//import com.encora.task_manager_service.config.TestMongoConfig;
//import com.encora.task_manager_service.models.Task;
//import com.encora.task_manager_service.models.TaskStatus;
//import com.encora.task_manager_service.repositories.TaskRepository;
//import com.encora.task_manager_service.repositories.UserRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//import static org.hamcrest.Matchers.hasSize;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(classes = {TestMongoConfig.class})
//@AutoConfigureMockMvc
//public class TaskControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private TaskRepository taskRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    private Task task1;
//    private Task task2;
//    private Authentication authentication;
//
//    @BeforeEach
//    public void setUp() {
//        task1 = new Task("1", "Test Task 1", "Description 1", LocalDate.now(), TaskStatus.TODO, "testuser");
//        task2 = new Task("2", "Test Task 2", "Description 2", LocalDate.now().plusDays(1), TaskStatus.IN_PROGRESS, "anotheruser");
//        taskRepository.save(task1);
//        taskRepository.save(task2);
//    }
//
//    @AfterEach
//    public void tearDown() {
//        taskRepository.deleteAll();
//    }
//
//
//    @Test
//    @WithMockUser(username = "testuser", roles = "USER")
//    public void testGetAllTasks_Success() throws Exception {
//        mockMvc.perform(get("/api/tasks")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].title").value("Test Task 1"));
//    }
//
//    @Test
//    @WithMockUser(username = "testuser", roles = "USER")
//    public void testGetTaskById_Success() throws Exception {
//        mockMvc.perform(get("/api/tasks/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("Test Task 1"));
//    }
//
//    @Test
//    @WithMockUser(username = "testuser", roles = "USER")
//    public void testGetTaskById_NotFound() throws Exception {
//        mockMvc.perform(get("/api/tasks/3")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @WithMockUser(username = "testuser", roles = "USER")
//    public void testCreateTask_Success() throws Exception {
//        Task newTask = new Task(null, "New Task", "New description", LocalDate.now(), TaskStatus.TODO, "testuser");
//        mockMvc.perform(post("/api/tasks")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newTask)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("New Task"));
//    }
//
//    @Test
//    @WithMockUser(username = "testuser", roles = "USER")
//    public void testUpdateTask_Success() throws Exception {
//        Task updatedTask = new Task("1", "Updated Task Title", "Updated description", LocalDate.now(), TaskStatus.IN_PROGRESS, "testuser");
//        mockMvc.perform(put("/api/tasks/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedTask)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title").value("Updated Task Title"));
//    }
//
//    @Test
//    @WithMockUser(username = "testuser", roles = "USER")
//    public void testUpdateTask_NotFound() throws Exception {
//        Task updatedTask = new Task("3", "Updated Task", "Updated Description", LocalDate.now(), TaskStatus.TODO, "testuser");
//        mockMvc.perform(put("/api/tasks/3")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedTask)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @WithMockUser(username = "testuser", roles = "USER")
//    public void testDeleteTask_Success() throws Exception {
//        mockMvc.perform(delete("/api/tasks/1"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "testuser", roles = "USER")
//    public void testDeleteTask_NotFound() throws Exception {
//        mockMvc.perform(delete("/api/tasks/3"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @WithMockUser(username = "testuser", roles = "USER")
//    public void testDeleteTask_Forbidden() throws Exception {
//        mockMvc.perform(delete("/api/tasks/2"))
//                .andExpect(status().isForbidden());
//    }
//}
