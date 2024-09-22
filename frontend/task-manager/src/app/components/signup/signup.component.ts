import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {
  user = { email: '', password: '' };
  error: string = '';

  constructor(private router: Router, private authService: AuthService) { }

  onSubmit() {
    this.authService.signup(this.user)
      .subscribe(
        () => {
          this.router.navigate(['/login']); // Redirect to login on success
        },
        error => {
          this.error = 'Error creating account'; // Handle signup errors
        }
      );
  }
}
