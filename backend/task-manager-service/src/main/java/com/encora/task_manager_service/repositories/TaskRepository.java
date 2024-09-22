package com.encora.task_manager_service.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.encora.task_manager_service.models.Task;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByUserId(String currentUserId);
}
