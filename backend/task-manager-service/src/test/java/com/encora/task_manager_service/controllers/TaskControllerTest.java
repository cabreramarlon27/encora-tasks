package com.encora.task_manager_service.controllers;

import com.encora.task_manager_service.models.Task;
import com.encora.task_manager_service.models.TaskStatus;
import com.encora.task_manager_service.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskControllerTest {
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskController taskController;
    private Task task1;
    private Task task2;
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        task1 = new Task("1", "Test Task 1", "Description 1", LocalDate.now(), TaskStatus.TODO, "testuser");
        task2 = new Task("2", "Test Task 2", "Description 2", LocalDate.now().plusDays(1), TaskStatus.IN_PROGRESS, "anotheruser");
        authentication = new UsernamePasswordAuthenticationToken("testuser", "testpassword", Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    public void testGetTaskById_Success() {
        when(taskRepository.findById("1")).thenReturn(Optional.of(task1));
        ResponseEntity<Task> response = taskController.getTaskById("1", authentication);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task1, response.getBody());
    }

    @Test
    public void testGetTaskById_NotFound() {
        when(taskRepository.findById("3")).thenReturn(Optional.empty());
        ResponseEntity<Task> response = taskController.getTaskById("3", authentication);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    public void testGetTaskById_Forbidden() {
        when(taskRepository.findById("2")).thenReturn(Optional.of(task2));
        ResponseEntity<Task> response = taskController.getTaskById("2", authentication);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task1);
        Task createdTask = taskController.createTask(task1, authentication);
        assertEquals(task1, createdTask);
        verify(taskRepository, times(1)).save(any(Task.class));
    }
    @Test
    public void testUpdateTask_Success() {
        when(taskRepository.findById("1")).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);
        Task updatedTask = new Task(); // Create an updated task object
        updatedTask.setTitle("Updated Task Title");
        updatedTask.setDescription("Updated description");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        updatedTask.setUserId("testuser"); // Ensure the userId is set
        Task result = taskController.updateTask("1", updatedTask);
        assertEquals("Updated Task Title", result.getTitle());
        assertEquals("Updated description", result.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }
    @Test
    public void testUpdateTask_NotFound() {
        when(taskRepository.findById("3")).thenReturn(Optional.empty());
        Task updatedTask = new Task("3", "Updated Task", "Updated Description", LocalDate.now(), TaskStatus.TODO, "testuser");
        Task result = taskController.updateTask("3", updatedTask);
        assertNull(result);
    }
    @Test
    public void testDeleteTask_Success() {
        when(taskRepository.findById("1")).thenReturn(Optional.of(task1));
        ResponseEntity<String> response = taskController.deleteTask("1", authentication);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task deleted successfully", response.getBody());
        verify(taskRepository, times(1)).deleteById("1");
    }

    @Test
    public void testDeleteTask_NotFound() {
        when(taskRepository.findById("3")).thenReturn(Optional.empty());
        ResponseEntity<String> response = taskController.deleteTask("3", authentication);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskRepository, never()).deleteById("3");
    }
    @Test
    public void testDeleteTask_Forbidden() {
        when(taskRepository.findById("2")).thenReturn(Optional.of(task2));
        ResponseEntity<String> response = taskController.deleteTask("2", authentication);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(taskRepository, never()).deleteById("2");
    }
}
