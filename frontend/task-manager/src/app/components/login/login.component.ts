import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  user = { username: '', password: '' };
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
