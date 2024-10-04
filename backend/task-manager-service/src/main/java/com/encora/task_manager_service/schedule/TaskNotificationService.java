package com.encora.task_manager_service.schedule;

import com.encora.task_manager_service.models.Task;
import com.encora.task_manager_service.repositories.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@EnableScheduling
public class TaskNotificationService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ObjectMapper objectMapper; // Add ObjectMapper for

    @Scheduled(fixedRate = 60000) // Check every minute (adjust as needed)
    public void sendTaskNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        // Find tasks due within 24 hours
        List<Task> dueSoonTasks = taskRepository.findByDueDateBetween(LocalDate.now(), tomorrow.toLocalDate());
        // Find overdue tasks
        List<Task> overdueTasks = taskRepository.findByDueDateBefore(LocalDate.now());
        // Send notifications for due soon tasks
        for (Task task : dueSoonTasks) {
            long hoursUntilDue = ChronoUnit.HOURS.between(now, task.getDueDate().atStartOfDay());
            String message = "Task '" + task.getTitle() + "' is due in " + hoursUntilDue + " hours.";
            sendTaskNotification(task.getUserId(), task);
        }
        // Send notifications for overdue tasks
        for (Task task : overdueTasks) {
            String message = "Task '" + task.getTitle() + "' is overdue.";
            sendTaskNotification(task.getUserId(), task);
        }
    }
    private void sendTaskNotification(String userId, Task task) {
        try {
            String jsonTask = objectMapper.writeValueAsString(task);
            System.out.println("Sending overdue task notification for user " + userId + ": " + jsonTask);
            messagingTemplate.convertAndSendToUser(userId, "/topic/taskNotifications", jsonTask);
        } catch (Exception e) {
            System.err.println("Error sending task notification: " + e.getMessage());
        }
    }
}