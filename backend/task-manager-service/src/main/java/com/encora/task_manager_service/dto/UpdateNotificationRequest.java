package com.encora.task_manager_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateNotificationRequest {
    @NotNull(message = "Notification preference cannot be null")
    private Boolean notificationsEnabled;

}