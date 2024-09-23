import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth'; // Adjust if needed
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;

  constructor(
    private http: HttpClient,
    private router: Router,
    private jwtHelper: JwtHelperService
  ) {
    this.currentUserSubject = new BehaviorSubject<any>(
      this.getUserFromLocalStorage()
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): any {
    return this.currentUserSubject.value;
  }

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      map((response) => {
        localStorage.setItem('token', response.jwt);
        this.currentUserSubject.next(this.jwtHelper.decodeToken(response.jwt));
        this.router.navigate(['/dashboard']);
        return response;
      }),
      catchError((error) => {
        if (error.status === 401) {
          return throwError('Invalid email or password.'); // Customize the error message
        } else {
          return throwError('An error occurred. Please try again later.'); // Generic error message
        }
      })
    );
  }

  signup(user: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/signup`, user).pipe(
      catchError((error) => {
        return throwError(error);
      })
    );
  }

  logout() {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']); // Or your desired redirect
  }

  isLoggedIn(): boolean {
    const token = localStorage.getItem('token');
    return !this.jwtHelper.isTokenExpired(token);
  }

  private getUserFromLocalStorage() {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken = this.jwtHelper.decodeToken(token);
      // Assuming the JWT now has 'username', 'email', and 'userId'
      return {
        username: decodedToken.username,
        email: decodedToken.email,
        userId: decodedToken.userId,
      };
    }
    return null;
  }
}
