import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { ClientService } from '../../../services/client.service';
import { CityService } from '../../../services/city.service';
import { Client } from '../../../models/client.model';
import { City } from '../../../models/city.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-client-add',
  templateUrl: './client-add.component.html',
  standalone: false,
  styleUrls: ['./client-add.component.css']
})
export class ClientAddComponent implements OnInit {
  public newClientForm!: FormGroup;
  public cities: City[] = [];

  constructor(
    private clientService: ClientService,
    private cityService: CityService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.newClientForm = formBuilder.group({
      jmbg: new FormControl('', [Validators.required, Validators.pattern(/^\d{13}$/)]),
      name: new FormControl('', [Validators.required, Validators.pattern(/^[A-Za-z\s]+$/)]),
      surname: new FormControl('', [Validators.required, Validators.pattern(/^[A-Za-z\s]+$/)]),
      age: new FormControl('', [Validators.required, Validators.pattern(/^\d+$/)]),
      mobile: new FormControl('', [Validators.required, Validators.pattern(/^\d+$/)]),
      city: new FormControl(null, Validators.required),
      username: new FormControl('', Validators.required),
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
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
          this.router.navigate(['/clients']); // Preusmeri na listu klijenata
        }
      },
      error: (err) => {
        alert(err.error?.message || 'Došlo je do neočekivane greške!');
      },
    });
  }
}
