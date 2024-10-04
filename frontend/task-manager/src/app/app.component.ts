import { Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NotificationService } from './services/notification.service';
import { AuthService } from './services/auth.service'; // Adjust the path if needed
import { Router } from '@angular/router';

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
    private authService: AuthService,
    private router: Router
  ) {
    this.notificationService.getNotifications().subscribe((message) => {
      console.log('Getting notifications from websocket');
      this.snackBar.open(message, 'Close', {
        duration: 5000, // Adjust duration as needed
      });
    });
    this.authService.logoutEvent.subscribe(() => {
      this.router
        .navigateByUrl('/login', { skipLocationChange: true })
        .then(() => {
          window.location.reload(); // Refresh the page
        });
    });
  }

  isUserLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
}
