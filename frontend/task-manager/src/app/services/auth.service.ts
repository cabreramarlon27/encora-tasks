import { Injectable } from '@angular/core';
import { HttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { map, catchError, tap, switchMap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';

interface LoginResponse {
  jwt: string;
  refreshToken: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth'; // Todo: set this from a env variable
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;
  private refreshTokenSubject: BehaviorSubject<string>;

  constructor(
    private http: HttpClient,
    private router: Router,
    private jwtHelper: JwtHelperService
  ) {
    this.currentUserSubject = new BehaviorSubject<any>(
      this.getUserFromLocalStorage()
    );
    this.currentUser = this.currentUserSubject.asObservable();
    this.refreshTokenSubject = new BehaviorSubject<string>(
      localStorage.getItem('refreshToken') || ''
    );
  }

  public get currentUserValue(): any {
    return this.currentUserSubject.value;
  }

  login(credentials: any): Observable<any> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap((response) => {
          // Store JWT and refresh token in local storage
          localStorage.setItem('token', response.jwt);
          localStorage.setItem('refreshToken', response.refreshToken);
          // Update subjects
          this.currentUserSubject.next(
            this.jwtHelper.decodeToken(response.jwt)
          );
          this.refreshTokenSubject.next(response.refreshToken);
          this.router.navigate(['/dashboard']);
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

  refreshToken(): Observable<any> {
    console.log('refreshToken called');
    return this.refreshTokenSubject.pipe(
      switchMap((refreshToken) => {
        return this.http.post<any>(`${this.apiUrl}/refresh`, { refreshToken });
      }),
      tap((response) => {
        // Store the new JWT token in local storage
        localStorage.setItem('token', response.jwt);
        // Update the currentUserSubject with the new token
        this.currentUserSubject.next(this.jwtHelper.decodeToken(response.jwt));
      }),
      catchError((error) => {
        // Handle error, e.g., logout the user
        this.logout();
        return throwError(error);
      })
    );
  }

  logout() {
    // Call the new logout API on the backend
    this.http.post(`${this.apiUrl}/logout`, {}).subscribe(
      () => {
        // Remove tokens from local storage
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        // Update subjects
        this.currentUserSubject.next(null);
        this.refreshTokenSubject.next('');
        // Redirect to login
        this.router.navigate(['/login']);
      },
      (error) => {
        console.error('Error logging out:', error);
        // Handle error, e.g., display an error message
      }
    );
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
