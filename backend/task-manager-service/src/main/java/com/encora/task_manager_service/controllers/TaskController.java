package com.encora.task_manager_service.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.encora.task_manager_service.models.TaskStatus;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.encora.task_manager_service.models.Task;
import com.encora.task_manager_service.repositories.TaskRepository;
import org.springframework.web.util.HtmlUtils;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Constructor for TaskController.
     *
     * @param taskRepository  The repository for accessing task data.
     * @param mongoTemplate   The MongoTemplate for custom queries.
     */
    @Autowired
    public TaskController(TaskRepository taskRepository, MongoTemplate mongoTemplate) {
        this.taskRepository = taskRepository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Retrieves a specific task by ID. Only the owner of the task can access it.
     *
     * @param id             The ID of the task to retrieve.
     * @param authentication The authentication object containing the user's information.
     * @return ResponseEntity containing the task if found and authorized, otherwise a 404 or 403 status.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated() and authentication.name == #task.userId")
    public ResponseEntity<Task> getTaskById(@PathVariable String id, Authentication authentication) {
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        if (!task.getUserId().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // If the user is authorized, return the task
        return ResponseEntity.ok(task);
    }

    /**
     * Retrieves a paginated list of tasks for the authenticated user.
     *
     * @param page           The page number (zero-based) to retrieve.
     * @param size           The number of tasks per page.
     * @param status         Optional. The status of tasks to filter by.
     * @param startDate      Optional. The start date for filtering tasks by due date.
     * @param endDate        Optional. The end date for filtering tasks by due date.
     * @param sortBy         The field to sort by.
     * @param sortDirection  The direction of the sorting (asc or desc).
     * @param authentication The authentication object containing the user's information.
     * @return ResponseEntity containing a page of tasks for the authenticated user.
     */
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

    /**
     * Creates a new task. The authenticated user is set as the owner of the task.
     *
     * @param task           The task object to create.
     * @param authentication The authentication object containing the user's information.
     * @return The created task.
     */
    @PostMapping
    public Task createTask(@Valid @RequestBody Task task, Authentication authentication) {
        task.setUserId(authentication.getName());
        task.setStatus(task.getStatus() == null ? TaskStatus.TODO : task.getStatus());
        task.setTitle(HtmlUtils.htmlEscape(task.getTitle()));
        task.setDescription(HtmlUtils.htmlEscape(task.getDescription()));
        return taskRepository.save(task);
    }

    /**
     * Updates an existing task. Only the owner of the task can update it.
     *
     * @param id   The ID of the task to update.
     * @param task The updated task object.
     * @return The updated task, or null if the task is not found.
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and authentication.name == #task.userId")
    public Task updateTask(@PathVariable String id, @RequestBody @Valid Task task) {
        Task existingTask = taskRepository.findById(id).orElse(null);
        if (existingTask != null) {
            existingTask.setTitle(HtmlUtils.htmlEscape(task.getTitle()));
            existingTask.setDescription(HtmlUtils.htmlEscape(task.getDescription()));
            existingTask.setStatus(task.getStatus());
            return taskRepository.save(existingTask);
        }
        return null;
    }

    /**
     * Deletes a task. Only the owner of the task can delete it.
     *
     * @param id             The ID of the task to delete.
     * @param authentication The authentication object containing the user's information.
     * @return ResponseEntity with a success message if deleted, otherwise a 404 or 403 status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable String id, Authentication authentication) {
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        if (!task.getUserId().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        taskRepository.deleteById(id);
        return ResponseEntity.accepted().build();
    }
}
