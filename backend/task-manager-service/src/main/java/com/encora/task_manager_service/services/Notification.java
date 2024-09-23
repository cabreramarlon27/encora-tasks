package com.encora.task_manager_service.services;

import com.encora.task_manager_service.models.Task;
import com.encora.task_manager_service.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Notification {

    private TaskRepository taskRepository;
    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    public Notification(TaskRepository taskRepository, SimpMessagingTemplate messagingTemplate) {
        this.taskRepository = taskRepository;
        this.messagingTemplate = messagingTemplate;
    }

//    @Scheduled(fixedRate = 3600) // Run every hour (adjust as needed)
    public void checkForDueTasks() {


        List<Task> tasks = taskRepository.findAll(); // Assuming you have a TaskRepository

//        System.out.println("Checking for due tasks...");
        // 2. Filter tasks due within the next 24 hours or overdue.
        List<Task> dueTasks = tasks.stream()
                .filter(task -> isTaskDueSoon(task) || isTaskOverdue(task))
                .collect(Collectors.toList());

        // 3. For each due/overdue task:
        for (Task task : dueTasks) {
            // a. Check if notifications are enabled for the user.
            if (areNotificationsEnabled(task.getUserId())) {
                // b. Send a notification to the user.
                String message = createNotificationMessage(task);
                sendNotification(task.getUserId(), message);
            }
        }
    }

    private boolean isTaskOverdue(Task task) {
        return  task.getDueDate().isBefore(LocalDate.now());
    }

    private boolean isTaskDueSoon(Task task) {
        // Set the logic to determine if the task will be due soon like in the next 24hrs
        return task.getDueDate().isBefore(LocalDate.now().plusDays(1));
    }

    private String createNotificationMessage(Task task) {
        return "Task '" + task.getTitle() + "' is due soon!";
    }

    public void sendNotification(String userId, String message) {
        messagingTemplate.convertAndSendToUser(userId, "/topic/notifications", message);
    }

    // Method to check if notifications are enabled for a user
    public boolean areNotificationsEnabled(String userId) {
        // ... your logic to check user notification preferences (e.g., from a database)
        //Todo: set logic to send notification using websocket to frontend app.
        return true;
    }
}