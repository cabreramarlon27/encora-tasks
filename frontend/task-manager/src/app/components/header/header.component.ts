import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router'; // Import Router
import { Observable } from 'rxjs'; // Import Observable
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  userName: Observable<string | null>; // Make userName an Observable

  constructor(private authService: AuthService, private router: Router) { 
    this.userName = this.authService.currentUser.pipe(
      map(user => user ? user.sub : null) // Assuming 'sub' holds the username
    );
  }
  ngOnInit() {
    // Get the user name from your AuthService (adjust as needed)
    // this.userName = this.authService.currentUserValue?.sub; 
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']); 
  }
}
