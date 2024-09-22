import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Task } from '../models/task.model'; // Import the Task model

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/api/tasks'; // Adjust if your backend API path is different

  constructor(private http: HttpClient) {}

  getTasks(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'dueDate', // Change to sortBy
    sortDirection: 'asc' | 'desc' = 'asc', // Add sortDirection
    startDate?: string,
    endDate?: string,
    statusFilter?: string
  ): Observable<any> {
    // Use 'any' to match the backend response structure
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortDirection', sortDirection)
      .set('sortBy', sortBy);

    if (startDate) {
      params = params.set('startDate', startDate);
    }

    if (endDate) {
      params = params.set('endDate', endDate);
    }

    if (statusFilter) {
      params = params.set('status', statusFilter);
    }

    return this.http.get<any>(this.apiUrl, { params });
  }

  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }

  deleteTask(id: number): Observable<any> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete(url);
  }
}
