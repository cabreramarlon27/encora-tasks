import { Component } from '@angular/core';
import { Router } from '@angular/router';
// ... (import your authentication service here)

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  user = { email: '', password: '' };
  error: string = '';

  constructor(private router: Router, private authService: AuthService) { }

  onSubmit() {
    this.authService.login(this.user)
      .subscribe(
        () => {
          this.router.navigate(['/tasks']); // Redirect on success
        },
        error => {
          this.error = 'Invalid email or password'; // Handle login errors
        }
      );
  }
}
