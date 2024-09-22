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
import java.util.List;

@Component public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder
    @Override
    public void run(String... args) throws Exception {
        // Check if the default user already exists
        if (userRepository.findByUsername("defaultuser").isEmpty()) {
            // Create and save the default user
            User defaultUser = new User();
            defaultUser.setUsername("admin");
            defaultUser.setPassword(passwordEncoder.encode("admin")); // Encode the password
            defaultUser.setRoles(Arrays.asList("ROLE_USER")); // Set user roles
            userRepository.save(defaultUser);
        }
//         Add default tasks if needed
        if (taskRepository.count() == 0) {
            List<Task> defaultTasks = Arrays.asList(
                    new Task("1", "Learn Spring Boot", "Study Spring Boot fundamentals", LocalDate.now(), TaskStatus.TODO, "defaultuser"),
                    new Task("2", "Build a REST API", "Create a REST API with Spring Boot", LocalDate.now(), TaskStatus.IN_PROGRESS, "defaultuser")
            );
            taskRepository.saveAll(defaultTasks);
        }
    }
}

