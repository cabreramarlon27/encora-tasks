package com.encora.task_manager_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users") // Specify the collection name if needed
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private List<String> roles; // Store roles as strings

    // Constructors, getters, setters (using Lombok's @Data)
}