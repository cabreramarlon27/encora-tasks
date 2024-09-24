package com.encora.task_manager_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "refresh_tokens")
public class RefreshToken {
    @Id
    private String id;
    private String token;
    private String userId;
    private Instant expiryDate;

    public RefreshToken(String token, String userId) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = Instant.now().plusSeconds(86400); // 24 hours
    }
}