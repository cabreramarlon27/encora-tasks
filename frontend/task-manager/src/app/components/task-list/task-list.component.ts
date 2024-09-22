import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../services/task.service';
import { MatDialog } from '@angular/material/dialog';
import { TaskCreateComponent } from '../task-create/task-create.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss']
})
export class TaskListComponent implements OnInit {
  tasks: any[] = [];

  constructor(private taskService: TaskService,
              public dialog: MatDialog,
              private authService: AuthService,
              private router: Router) { }

  ngOnInit() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']); 
    }
    this.getTasks();
  }

  getTasks() {
    this.taskService.getTasks().subscribe(tasks => {
      this.tasks = tasks;
    });
  }

  addTask() {
    const dialogRef = this.dialog.open(TaskCreateComponent);

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.taskService.createTask(result).subscribe(() => {
          this.getTasks();
        });
      }
    });
  }

  deleteTask(id: number) {
    this.taskService.deleteTask(id).subscribe(() => {
      this.getTasks();
    });
  }

  updateTaskStatus(task: Task) {
    // Make a PATCH request to your backend to update the task status
    // this.taskService.updateTask(task.id, { status: task.completed ? 'COMPLETED' : 'TODO' })
    //   .subscribe(() => {
    //     // Handle success (e.g., update the task in the local array)
    //   });
  }

  isPastDue(dueDate: Date): boolean {
    return new Date(dueDate) < new Date();
  }

  getTimeSinceDueDate(dueDate: Date): string {
    const now = new Date();
    const diff = now.getTime() - new Date(dueDate).getTime();
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const weeks = Math.floor(days / 7);
    const years = Math.floor(days / 365);

    if (years > 0) {
      return `${years} year${years > 1 ? 's' : ''}`;
    } else if (weeks > 0) {
      return `${weeks} week${weeks > 1 ? 's' : ''}`;
    } else {
      return `${days} day${days > 1 ? 's' : ''}`;
    }
  }

  logout() {
    this.authService.logout();
  }
}
