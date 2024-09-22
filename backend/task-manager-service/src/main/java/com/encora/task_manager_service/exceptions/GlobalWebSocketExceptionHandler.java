package com.encora.task_manager_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
@ControllerAdvice
public class GlobalWebSocketExceptionHandler {
    @MessageExceptionHandler(Exception.class)
    @SendToUser("/topic/errors") // Send error messages to the /topic/errors topic
    public ResponseEntity<String> handleException(Exception ex) {
        // Log the exception
        ex.printStackTrace(); // Or use a proper logging framework
        // Return an error message to the client
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
    }
}