import { Injectable } from '@angular/core';
import { Stomp, Frame } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private stompClient: any;

  constructor(private authService: AuthService) {
    this.connect();
  }

  connect() {
    const socket = new SockJS('http://localhost:8080/ws');
    const headers = {
      Authorization: `Bearer ${this.authService.getJwtToken()}`, // Add JWT token
    };
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect(
      headers,
      (frame: Frame) => {
        // Connection successful
        console.log('Connected to WebSocket: ' + frame);

        const userId = this.authService.currentUserValue?.userId;
        if (userId) {
          this.subscribeToTaskNotifications(userId);
        } else {
          console.warn('User not logged in, skipping WebSocket subscription');
        }
      },
      (error: Error) => {
        // Connection error
        console.error('WebSocket connection error:', error);
        // Implement retry logic or error handling here
      }
    );
  }

  private subscribeToTaskNotifications(userId: number) {
    const destination = '/topic/taskNotifications'; // No extra characters
    console.log('Subscribing to:', destination); // Verify the destination

    const subscription = this.stompClient.subscribe(
      destination,
      (message: Frame) => {
        // Handle incoming notifications
        console.log('Received WebSocket message:', message);
        this.handleNotification(JSON.parse(message.body));
      }
    );

    // Store the subscription for potential unsubscription later
    // ... (You might want to store this in a component if needed)
  }

  handleNotification(task: any) {
    // Implement your notification handling logic here
    console.log('Handling notification for task:', task);
    // Display notification to user (e.g., using a toast or alert)
    alert(`Task "${task.title}" is about to expire or has expired!`);
  }

  disconnect() {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.disconnect(() => {
        console.log('Disconnected from WebSocket');
      });
    }
  }
}
