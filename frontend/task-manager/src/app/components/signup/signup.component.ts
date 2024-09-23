import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import {
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
  FormGroup,
  FormControl,
  Validators,
} from '@angular/forms';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss'],
})
export class SignupComponent {
  user = { email: '', username: '', password: '', confirmPassword: '' };
  error: string = '';

  constructor(private router: Router, private authService: AuthService) {}

  onSubmit() {
    if (this.signupForm.valid) {
      const userData = {
        username: this.signupForm.value.username,
        email: this.signupForm.value.email,
        password: this.signupForm.value.password,
      };
      this.authService.signup(userData).subscribe(
        () => {
          this.router.navigate(['/login']);
        },
        (error) => {
          this.error = 'Error creating account';
        }
      );
    }
  }

  // Define the validator function
  passwordsMatchValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      // Access the parent FormGroup to get the password control
      const passwordControl = control.parent?.get('password');
      const confirmPassword = control.value;

      // Only compare if both password fields have values
      if (passwordControl && confirmPassword) {
        return passwordControl.value !== confirmPassword
          ? { noMatch: true }
          : null;
      }

      return null; // No error if either field is empty
    };
  }

  signupForm = new FormGroup({
    username: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [
      Validators.required,
      this.passwordValidator(),
    ]),
    confirmPassword: new FormControl('', [
      Validators.required,
      this.passwordsMatchValidator(),
    ]),
  });

  passwordValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) {
        return null;
      }
      const hasUpperCase = /[A-Z]+/.test(value);
      const hasLowerCase = /[a-z]+/.test(value);
      const hasNumeric = /[0-9]+/.test(value);
      const hasSpecial = /[$&+,:;=?@#|'<>.^*()%!-]/.test(value);
      const passwordValid =
        hasUpperCase &&
        hasLowerCase &&
        hasNumeric &&
        hasSpecial &&
        value.length >= 8;
      return !passwordValid ? { pattern: true } : null;
    };
  }
}
