package com.encora.task_manager_service.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.encora.task_manager_service.models.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.encora.task_manager_service.models.Task;
import com.encora.task_manager_service.repositories.TaskRepository;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private MongoTemplate mongoTemplate; // Inject


    @GetMapping()
    public ResponseEntity<Page<Task>> getTasksForUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            Authentication authentication
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        Query query = new Query();

        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("userId").is(authentication.getName())); // Filter by user ID

        if (status != null) {
            criteriaList.add(Criteria.where("status").is(TaskStatus.valueOf(status)));
        }
        if (startDate != null) {
            criteriaList.add(Criteria.where("dueDate").gte(startDate));
        }
        if (endDate != null) {
            criteriaList.add(Criteria.where("dueDate").lte(endDate));
        }

        query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        long total = mongoTemplate.count(query, Task.class);


        List<Task> tasks = mongoTemplate.find(query.with(pageable), Task.class);

        Page<Task> taskPage = new PageImpl<>(tasks, pageable, total);

        return ResponseEntity.ok(taskPage);
    }

    @PostMapping
    public Task createTask(@RequestBody Task task,  Authentication authentication) {
        task.setUserId(authentication.getName());
        return taskRepository.save(task);
    }

    @PatchMapping("/{id}")
    public Task updateTask(@PathVariable String id, @RequestBody Task task) {
        Task existingTask = taskRepository.findById(id).orElse(null);
        if (existingTask != null) {
            existingTask.setTitle(task.getTitle());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
            return taskRepository.save(existingTask);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable String id) {
        taskRepository.deleteById(id);
    }
}
