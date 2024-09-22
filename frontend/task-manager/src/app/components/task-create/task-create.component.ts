import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Task, TaskStatus } from '../../models/task.model'; // Import Task and TaskStatus

@Component({
  selector: 'app-task-create',
  templateUrl: './task-create.component.html',
  styleUrls: ['./task-create.component.scss']
})
export class TaskCreateComponent {
  newTask: Task = { 
    title: '', 
    description: '', 
    dueDate: new Date(), // Initialize dueDate
    status: TaskStatus.TODO // Initialize status
  };

  taskStatuses = TaskStatus;
  
  constructor(public dialogRef: MatDialogRef<TaskCreateComponent>) { }

  onSubmit() {
    this.dialogRef.close(this.newTask);
  }
}
