import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from './services/user.service';
import { ClientService } from './services/client.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: false
})
export class AppComponent {
  title = 'rent-a-car-frontend';


  constructor(
    public userService: UserService,
    public clientService: ClientService,
    private router: Router
  ) {}


  logout() {
    if (this.userService.getUserStatus() === 'admin') {
      this.userService.logOut();
    }
    if (this.clientService.getUserStatus() === 'user') {
      this.clientService.logOut();
    }
    this.router.navigate(['/login']);
  }
}
