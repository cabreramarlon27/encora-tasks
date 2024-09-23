import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Task, TaskStatus } from '../../models/task.model';
import { TaskService } from '../../services/task.service';

@Component({
  selector: 'app-task-edit',
  templateUrl: './task-edit.component.html',
  styleUrls: ['./task-edit.component.scss'],
})
export class TaskEditComponent {
  task: Task;
  taskStatuses = TaskStatus;

  constructor(
    public dialogRef: MatDialogRef<TaskEditComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { task: Task },
    private taskService: TaskService
  ) {
    this.task = { ...data.task }; // Create a copy of the task to edit
  }

  onSubmit() {
    if (this.task.id !== undefined) {
      this.taskService.updateTask(this.task.id, this.task).subscribe(
        (updatedTask) => {
          this.dialogRef.close(updatedTask);
        },
        (error) => {
          console.error('Error updating task:', error);
        }
      );
    } else {
      console.error('Cannot update task without an ID.');
    }
  }
}
