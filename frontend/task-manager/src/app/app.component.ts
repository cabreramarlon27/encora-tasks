import { Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NotificationService } from './services/notification.service';
import { AuthService } from './services/auth.service'; // Adjust the path if needed

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  title = 'task-manager';
  constructor(
    private snackBar: MatSnackBar,
    private notificationService: NotificationService,
    private authService: AuthService
  ) {
    this.notificationService.getNotifications().subscribe((message) => {
      this.snackBar.open(message, 'Close', {
        duration: 5000, // Adjust duration as needed
      });
    });
  }

  isUserLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
}
