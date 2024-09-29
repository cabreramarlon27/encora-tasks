package com.encora.task_manager_service.models;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Document("tasks")
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    private String id;
    @NotBlank(message = "Title is mandatory")
    private String title;
    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
    @NotNull(message = "Due date is mandatory")
    private LocalDate dueDate;
    private TaskStatus status;
    private String userId;

}
