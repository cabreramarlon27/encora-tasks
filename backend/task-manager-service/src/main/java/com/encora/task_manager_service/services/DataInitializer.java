package com.encora.task_manager_service.services;

import com.encora.task_manager_service.models.Task;
import com.encora.task_manager_service.models.TaskStatus;
import com.encora.task_manager_service.models.User;
import com.encora.task_manager_service.repositories.TaskRepository;
import com.encora.task_manager_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    private static final String[] PROGRAMMING_TASK_DESCRIPTIONS = {
            "Implement user authentication API endpoint",
            "Write unit tests for the TaskService class",
            "Design database schema for new user profile feature",
            "Debug performance issue in API endpoint X",
            "Refactor code in module Y for better readability",
            "Research and implement a caching solution",
            "Update dependencies to latest versions",
            "Write documentation for REST API endpoints",
            "Create a new Angular component for task display",
            "Configure CI/CD pipeline for automated deployments"
    };

    private static final String[] PROGRAMMING_TASK_TITLES = {
            "Auth API", "Unit Tests", "DB Schema", "API Performance", "Code Refactor",
            "Caching Solution", "Dependency Update", "API Docs", "Angular Component", "CI/CD Setup"
    };

    @Override
    public void run(String... args) throws Exception {
        // Check if the default user already exists
        if (userRepository.findByUsername("admin2").isEmpty()) {
            // Create and save the default user
            User defaultUser = new User();
            defaultUser.setUsername("admin");
            defaultUser.setPassword(passwordEncoder.encode("admin")); // Encode the password
            defaultUser.setRoles(Arrays.asList("ROLE_USER")); // Set user roles
            userRepository.save(defaultUser);
        }
//         Add default tasks if needed
        List<Task> existingTasks = taskRepository.findByUserId("admin2");
        if (existingTasks.size() < 30) {
                // Calculate how many more tasks to add
            int tasksToAdd = 30 - existingTasks.size();
            for (int i = 0; i < tasksToAdd; i++) {
                Task task = new Task();
                task.setTitle(getRandomTaskTitle());
                task.setDescription(getRandomTaskDescription());
                task.setStatus(getRandomTaskStatus());
                task.setDueDate(getRandomDueDate());
                task.setUserId("admin2");
                taskRepository.save(task);
            }
        }
    }

    private String getRandomTaskDescription() {
        Random random = new Random();
        int randomIndex = random.nextInt(PROGRAMMING_TASK_DESCRIPTIONS.length);
        return PROGRAMMING_TASK_DESCRIPTIONS[randomIndex];
    }

    private TaskStatus getRandomTaskStatus() {
        List<TaskStatus> statuses = Arrays.asList(TaskStatus.values());
        Collections.shuffle(statuses);
        return statuses.get(0);
    }
    private LocalDate getRandomDueDate() {
        Random random = new Random();
        return LocalDate.now().plusDays(random.nextInt(365)); // Random due date within a year
    }
    private String getRandomTaskTitle() {
        Random random = new Random();
        int randomIndex = random.nextInt(PROGRAMMING_TASK_TITLES.length);
        return PROGRAMMING_TASK_TITLES[randomIndex];
    }
}

