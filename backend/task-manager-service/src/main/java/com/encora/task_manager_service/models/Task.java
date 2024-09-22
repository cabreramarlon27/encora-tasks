package com.encora.task_manager_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document("tasks")
@AllArgsConstructor
public class Task {
    @Id
    private String id;
    private String title;
    private String description;
    private LocalDate dueDate; // New due date field
    private TaskStatus status; // New status field (enum)
    private String userId; // New user ID field

}
