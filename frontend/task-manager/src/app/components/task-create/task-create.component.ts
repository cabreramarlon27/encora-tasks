import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-task-create',
  templateUrl: './task-create.component.html',
  styleUrls: ['./task-create.component.css']
})
export class TaskCreateComponent {
  newTask = { title: '', description: '' };

  constructor(public dialogRef: MatDialogRef<TaskCreateComponent>) { }

  onSubmit() {
    this.dialogRef.close(this.newTask);
  }
}
