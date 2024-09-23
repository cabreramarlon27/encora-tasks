import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';

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

  getPastDueTasksCount(): Observable<number> {
    const today = new Date();
    const todayString = this.formatDate(today); // Use your existing formatDate() method

    let params = new HttpParams().set('endDate', todayString).set('size', '1'); // Only need the count, so fetch one item

    return this.http
      .get<any>(this.apiUrl, { params })
      .pipe(map((data) => data.totalElements));
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }
}
