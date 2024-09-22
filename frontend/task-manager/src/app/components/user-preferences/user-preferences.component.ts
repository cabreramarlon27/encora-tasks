import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-user-preferences',
  templateUrl: './user-preferences.component.html',
  styleUrls: ['./user-preferences.component.scss'],
})
export class UserPreferencesComponent implements OnInit {
  notificationsEnabled: boolean = true;

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    this.notificationsEnabled =
      this.notificationService.areNotificationsEnabled();
  }

  toggleNotifications() {
    this.notificationService.setNotificationEnabled(this.notificationsEnabled);
  }
}
