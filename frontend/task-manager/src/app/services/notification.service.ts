import { Injectable } from '@angular/core';
import { Subject, Observable, of } from 'rxjs';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private notifications$ = new Subject<string>();
  // private socket$: WebSocketSubject<any>;

  constructor(private authService: AuthService) {
    // this.socket$ = webSocket(`ws://localhost:8080/notificationsd`);
    // this.connectWebSocket();
  }

  private connectWebSocket() {
    const user = this.authService.currentUserValue;
    // if (user && user.userId) {
    //   // Assuming 'sub' contains the username or userId
    //   this.socket$ = webSocket(
    //     `ws://localhost:8080/notifications/${user.userId}`
    //   ); // Use user ID in the URL
    //   this.socket$.subscribe(
    //     (message) => this.showNotification(message),
    //     (err) => console.error('WebSocket error:', err)
    //   );
    // }
  }

  getNotifications(): Observable<string> {
    return this.notifications$.asObservable();
  }

  showNotification(message: string) {
    if (this.areNotificationsEnabled()) {
      // Check user preferences
      this.notifications$.next(message);
    }
  }

  // User preferences (replace with your actual implementation)
  areNotificationsEnabled(): boolean {
    // 1. Get user preferences from local storage or your backend
    const userPreferences = localStorage.getItem('notificationPreferences');

    // 2. Parse and return the notificationEnabled value
    if (userPreferences) {
      const preferences = JSON.parse(userPreferences);
      return preferences.notificationEnabled;
    }

    // 3. Default to enabled if no preferences are found
    return true;
  }

  setNotificationEnabled(enabled: boolean) {
    // 1. Get existing preferences or create an empty object
    const userPreferences = JSON.parse(
      localStorage.getItem('notificationPreferences') || '{}'
    );

    // 2. Update the notificationEnabled property
    userPreferences.notificationEnabled = enabled;

    // 3. Store the updated preferences
    localStorage.setItem(
      'notificationPreferences',
      JSON.stringify(userPreferences)
    );

    // 4. Reconnect to the WebSocket if the user enables notifications
    if (enabled) {
      this.connectWebSocket();
    } else {
      // Close the WebSocket connection if the user disables notifications
      // if (this.socket$) {
      //   this.socket$.complete();
      // }
    }
  }
}
