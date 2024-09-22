import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private notifications$ = new Subject<string>();

  getNotifications(): Subject<string> {
    return this.notifications$;
  }

  showNotification(message: string) {
    this.notifications$.next(message);
  }
}
