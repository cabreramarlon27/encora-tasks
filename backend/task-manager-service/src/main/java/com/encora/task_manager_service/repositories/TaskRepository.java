package com.encora.task_manager_service.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.encora.task_manager_service.models.Task;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByUserId(String currentUserId);
    Page<Task> findByUserId(String userId, Pageable pageable);
}
