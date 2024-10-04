package com.encora.task_manager_service.controllers;

import com.encora.task_manager_service.dto.UpdateNotificationRequest;
import com.encora.task_manager_service.exceptions.UserNotFoundException;
import com.encora.task_manager_service.models.User;
import com.encora.task_manager_service.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/me/notifications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateUserNotificationsEnabled(
            @RequestBody @Valid UpdateNotificationRequest request,
            Authentication authentication) {
        log.info("Testing endpoint");
        String authenticatedUserEmail = authentication.getName();

        try {
            User updatedUser = userService.updateNotificationsEnabled(authenticatedUserEmail, request.getNotificationsEnabled());
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/me/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getUser(Authentication authentication) {
        String authenticatedUserEmail = authentication.getName();

        try {
            return ResponseEntity.ok(userService.getUserByEmail(authenticatedUserEmail));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}