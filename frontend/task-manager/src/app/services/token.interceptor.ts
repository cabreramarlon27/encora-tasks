import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { AuthService } from './auth.service';
import { Injectable } from '@angular/core';
import { Observable, catchError, switchMap, throwError } from 'rxjs';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    // Get the JWT token from local storage
    const token = localStorage.getItem('token');

    // If the token exists, add it to the Authorization header
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
    }

    // Handle the request and check for 401 Unauthorized errors
    return next.handle(request).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          // If a 401 error occurs, try to refresh the token
          console.log('Token expired, refreshing...');
          return this.authService.refreshToken().pipe(
            switchMap(() => {
              // Retry the original request with the new token
              return next.handle(request);
            })
          );
        } else {
          // If it's not a 401 error, rethrow the error
          return throwError(error);
        }
      })
    );
  }
}
