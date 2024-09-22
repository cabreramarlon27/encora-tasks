import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../services/task.service';
import { Task, TaskStatus } from '../../models/task.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'], // Add styling later
})
export class DashboardComponent implements OnInit {
  totalTasks: number = 0;
  tasksDueSoon: number = 0;
  completedTasks: number = 0;
  startDate: string | undefined = undefined;
  endDate: string | undefined = undefined;

  constructor(private taskService: TaskService) {}

  ngOnInit() {
    this.loadTaskSummary();
  }

  loadTaskSummary() {
    this.taskService.getTasks().subscribe((data) => {
      this.totalTasks = data.totalElements;
    });

    // Calculate tasks due soon (e.g., within the next week)
    const today = new Date();
    const tomorrowDate = new Date();
    tomorrowDate.setDate(today.getDate() + 1);
    this.endDate = this.formatDate(tomorrowDate);
    this.startDate = this.formatDate(today);

    this.taskService
      .getTasks(
        0,
        10,
        'dueDate',
        'asc',
        this.startDate,
        this.endDate,
        undefined
      )
      .subscribe((data) => {
        this.tasksDueSoon = data.totalElements;
      });

    this.taskService
      .getTasks(
        0,
        10,
        'dueDate,asc',
        undefined,
        undefined,
        TaskStatus.COMPLETED
      )
      .subscribe((data) => {
        this.completedTasks = data.totalElements;
      });
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2); // Add leading zero if needed
    const day = ('0' + date.getDate()).slice(-2); // Add leading zero if needed
    return `${year}-${month}-${day}`;
  }
}
