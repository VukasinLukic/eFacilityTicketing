import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  public loginForm!: FormGroup;

  constructor(
    private userService: UserService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.loginForm = formBuilder.group({
      username: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required),
    });
  }

  login() {
    if (!this.loginForm.valid) {
      alert('Morate popuniti polja za korisničko ime i lozinku!');
      return;
    }

    const username = this.loginForm.get('username')!.value;
    const password = this.loginForm.get('password')!.value;

    this.userService.login(username, password).subscribe({
      next: (user) => {
        alert('Dobrodošli u Rent-a-Car administraciju!');
        this.userService.setSessionData(user);
        this.router.navigate(['/cars']);
      },
      error: (err) => {
        if (err.status === 401) {
          alert('Pogrešno korisničko ime ili lozinka!');
        } else {
          alert('Došlo je do neočekivane greške.');
          console.error(err);
        }
      }
    });
  }
}
