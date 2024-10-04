import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private notifications$ = new Subject<string>();
  private apiUrl = 'http://localhost:8080/api/users'; // Assuming this is your base API URL for users
  private notificationsEnabled: boolean = true; // Default to true, you might want to fetch the initial value from the backend

  constructor(private http: HttpClient) {}

  getNotifications(): Observable<string> {
    return this.notifications$.asObservable();
  }

  showNotification(message: string) {
    if (this.notificationsEnabled) {
      this.notifications$.next(message);
    }
  }

  areNotificationsEnabled(): boolean {
    return this.notificationsEnabled;
  }

  setNotificationEnabled(enabled: boolean): Observable<any> {
    console.log('Setting notifications enabled:', enabled);
    // Assuming your backend has an endpoint like '/api/users/me/notifications' to update notification preferences
    return this.http
      .patch(`${this.apiUrl}/me/notifications`, { enabled: enabled })
      .pipe(
        tap(() => {
          console.log('Setting notifications enabled2:', enabled);
          this.notificationsEnabled = enabled; // Update the local state after a successful request
        }),
        catchError((error) => {
          console.error('Error updating notification preferences:', error);
          return throwError(error); // Handle the error appropriately (e.g., show an error message to the user)
        })
      );
  }
}
