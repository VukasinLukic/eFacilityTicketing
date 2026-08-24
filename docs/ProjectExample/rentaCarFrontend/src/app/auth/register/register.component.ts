import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { City } from '../../models/city.model';
import { ClientService } from '../../services/client.service';
import { CityService } from '../../services/city.service';
import { Router } from '@angular/router';
import { Client } from '../../models/client.model';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  public newClientForm!: FormGroup;
  public cities!: City[];

  constructor(
    private clientService: ClientService,
    private cityService: CityService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.newClientForm = this.formBuilder.group({
      jmbg: ['', Validators.required],
      name: ['', Validators.required],
      surname: ['', Validators.required],
      age: ['', Validators.required],
      mobile: ['', Validators.required],
      city: [null, Validators.required],
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.cityService.getAllCities().subscribe({
      next: (response) => {
        this.cities = response.data.values as City[];
      },
    });
  }


  public addClient() {
    if (!this.newClientForm.valid) {
      alert('Molimo popunite sva polja ispravno.');

      return;
    }


    const client: Client = this.newClientForm.value;

    this.clientService.addNewClient(client).subscribe({
      next: (res) => {
        alert(res.message);
        if (res.message === 'Account created successfully!') {
          this.router.navigate(['/login-client']); // Preusmeri na login za klijente
        }
      },
      error: (err) => {
        if (err.error && err.error.message) {
          alert(err.error.message);
        } else {
          alert('Došlo je do neočekivane greške!');
        }
      },
    });
  }
}
