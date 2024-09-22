package com.encora.task_manager_service.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.encora.task_manager_service.models.TaskStatus;
import com.encora.task_manager_service.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;

@RestController
@RequestMapping("/api/tasks")
@Slf4j
public class TaskController {

    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;

    /**
     * Constructor for TaskController.
     *
     * @param taskRepository The repository for accessing task data.
     * @param mongoTemplate  The MongoTemplate for custom queries.
     */
    @Autowired
    public TaskController(TaskRepository taskRepository, MongoTemplate mongoTemplate, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a specific task by ID. Only the owner of the task can access it.
     *
     * @param id             The ID of the task to retrieve.
     * @param authentication The authentication object containing the user's information.
     * @return ResponseEntity containing the task if found and authorized, otherwise a 404 or 403 status.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Task> getTaskById(@PathVariable String id, Authentication authentication) {
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        String userId = getUserIdFromAuthentication(authentication);
        if (!task.getUserId().equals(userId)) {
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
        String userId = getUserIdFromAuthentication(authentication);

        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("userId").is(userId)); // Filter by user ID

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
        log.info("User {} created a new task: {}",
                authentication.getName(),
                task.getTitle());
        task.setUserId(getUserIdFromAuthentication(authentication));
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
    @PreAuthorize("isAuthenticated()")
    public Task updateTask(@PathVariable String id, @RequestBody @Valid Task task, Authentication authentication) {
        String userId = getUserIdFromAuthentication(authentication);
        Task existingTask = taskRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with ID: " + id));
        if (userId.equals(existingTask.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You are not authorized to update this task."
            );
        }
        existingTask.setTitle(HtmlUtils.htmlEscape(task.getTitle()));
        existingTask.setDescription(HtmlUtils.htmlEscape(task.getDescription()));
        existingTask.setStatus(task.getStatus());
        return taskRepository.save(existingTask);
    }

    /**
     * Deletes a task. Only the owner of the task can delete it.
     *
     * @param id             The ID of the task to delete.
     * @param authentication The authentication object containing the user's information.
     * @return ResponseEntity with a success message if deleted, otherwise a 404 or 403 status.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteTask(@PathVariable String id, Authentication authentication) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        String userId = getUserIdFromAuthentication(authentication);
        if (!task.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        taskRepository.deleteById(id);
        return ResponseEntity.accepted().build();
    }

    private String getUserIdFromAuthentication(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UnsupportedOperationException("User not found: " + username))
                .getId();
    }
}
