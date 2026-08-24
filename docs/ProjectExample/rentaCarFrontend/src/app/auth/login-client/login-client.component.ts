import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ClientService } from '../../services/client.service';
import { Router } from '@angular/router';
import { Client } from '../../models/client.model';

@Component({
  selector: 'app-login-client',
  standalone: false,
  templateUrl: './login-client.component.html',
  styleUrls: ['./login-client.component.css']
})
export class LoginClientComponent {
  public loginForm!: FormGroup;

  constructor(
    private clientService: ClientService,
    private router: Router,
    private formBuilder: FormBuilder
  ){
    this.loginForm = formBuilder.group({
      username: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required)
    });
  }

  login() {
    if(!this.loginForm.valid) {
      alert("Morate popuniti oba polja!");
      return;
    }

    const username = this.loginForm.get('username')!.value;
    const password = this.loginForm.get('password')!.value;

    this.clientService.login(username, password).subscribe({
      next: (response) => {
        // ISPRAVKA: Čitamo poruku iz response objekta
        const message = response.message;
        alert(message); // Prikazaće "Hello, Ime Prezime!"

        // Sada radimo ostatak logike
        if (message !== 'Invalid username or password!') {
          this.clientService.getAll().subscribe((data) => {
            const clients = data.data.values as Client[];
            const loggedInClient = clients.find(client => client.username === username);

            if (loggedInClient) {
              this.clientService.setSessionData(response, loggedInClient);
              this.router.navigate(['/cars']);
            }
          });
        } else {
          // Ovaj else verovatno nije ni potreban jer backend baca grešku
        }
      },
      error: (err) => {
        // Ako backend vrati grešku, hvatamo je ovde
        alert('Pogrešno korisničko ime ili lozinka!');
        console.error(err);
      }
    });
  }
}
