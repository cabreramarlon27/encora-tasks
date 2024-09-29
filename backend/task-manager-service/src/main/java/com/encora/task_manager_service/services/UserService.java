package com.encora.task_manager_service.services;

import com.encora.task_manager_service.exceptions.UserNotFoundException;
import com.encora.task_manager_service.models.User;
import com.encora.task_manager_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User updateNotificationsEnabled(String userEmail, Boolean notificationsEnabled) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userEmail));

        user.setNotificationsEnabled(notificationsEnabled);
        return userRepository.save(user);
    }
}