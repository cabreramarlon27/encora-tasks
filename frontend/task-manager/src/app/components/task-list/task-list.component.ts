import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../services/task.service';
import { MatDialog } from '@angular/material/dialog';
import { TaskCreateComponent } from '../task-create/task-create.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { TaskStatus } from 'src/app/models/task.model';
import { PageEvent } from '@angular/material/paginator'; // Import PageEvent
import { Sort } from '@angular/material/sort'; // Import Sort
import { Task } from 'src/app/models/task.model';
import { MatConfirmDialogComponent } from '../mat-confirmation-dialog/mat-confirmation-dialog.component'; // Assuming you have this component

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss'],
})
export class TaskListComponent implements OnInit {
  tasks: any[] = [];
  totalElements: number = 0; // Total number of tasks
  pageSize: number = 10; // Number of tasks per page
  currentPage: number = 0; // Current page index
  sortField: string = 'dueDate'; // Field to sort by
  sortOrder: 'asc' | 'desc' = 'asc'; // Sort order
  startDate: string | undefined = undefined;
  endDate: string | undefined = undefined;
  statusFilter: TaskStatus | undefined = undefined; // Status filter value
  taskStatuses = TaskStatus; // Make TaskStatus available in the template

  constructor(
    private taskService: TaskService,
    public dialog: MatDialog,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
    }
    this.loadTasks();
  }

  addTask() {
    const dialogRef = this.dialog.open(TaskCreateComponent);

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.taskService.createTask(result).subscribe(() => {
          this.loadTasks();
        });
      }
    });
  }

  deleteTask(id: number) {
    const dialogRef = this.dialog.open(MatConfirmDialogComponent, {
      data: { message: 'Are you sure you want to delete this task?' },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        // User clicked "Yes"
        this.taskService.deleteTask(id).subscribe({
          next: () => {
            // Handle success (e.g., update the task in the local array)
            this.loadTasks(); // Call loadTasks() after successful deletion
          },
          error: (error) => {
            // Handle error
            console.error('Error deleting task:', error);
          },
        });
      }
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

  loadTasks() {
    this.taskService
      .getTasks(
        this.currentPage,
        this.pageSize,
        this.sortField,
        this.sortOrder,
        this.startDate ? this.formatDate(new Date(this.startDate)) : undefined,
        this.endDate ? this.formatDate(new Date(this.endDate)) : undefined,
        this.statusFilter
      )
      .subscribe((data) => {
        this.tasks = data.content;
        this.totalElements = data.totalElements;
      });
  }

  onPageChange(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadTasks();
  }

  onSortChange(event: Sort) {
    this.sortField = event.active;
    this.sortOrder = event.direction === '' ? 'asc' : event.direction;
    this.loadTasks();
  }

  applyFilters() {
    this.loadTasks();
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2); // Add leading zero if needed
    const day = ('0' + date.getDate()).slice(-2); // Add leading zero if needed
    return `${year}-${month}-${day}`;
  }
}
