// src/app/models/task.model.ts
export interface Task {
    id?: number; 
    title: string;
    description: string;
    dueDate: Date; // New field for due date
    status: TaskStatus; // New field for status
  }
  
  export enum TaskStatus {
    TODO = 'TODO',
    IN_PROGRESS = 'IN_PROGRESS',
    COMPLETED = 'COMPLETED',
    BLOCKED = 'BLOCKED'
  }
  