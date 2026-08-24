import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Car } from '../../../models/car.model';
import { Client } from '../../../models/client.model';
import { Rental } from '../../../models/rental.model';
import { RentalService } from '../../../services/rental.service';
import { CarService } from '../../../services/car.service';
import { ClientService } from '../../../services/client.service';
import { UserService } from '../../../services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-rental-add',
  standalone: false,
  templateUrl: './rental-add.component.html',
  styleUrls: ['./rental-add.component.css']
})
export class RentalAddComponent implements OnInit {
  public newRentalForm!: FormGroup;
  public cars: Car[] = [];
  public clients: Client[] = [];

  constructor(
    private rentalService: RentalService,
    private carService: CarService,
    public clientService: ClientService,
    public userService: UserService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.newRentalForm = formBuilder.group({
      startDate: new FormControl(new Date(), Validators.required),
      endDate: new FormControl(new Date(), Validators.required), // DODATO
      notes: new FormControl('', Validators.required),
      car: new FormControl(null, Validators.required),
      client: new FormControl(null, Validators.required),
    });
  }

  ngOnInit() {
    // Učitaj samo dostupne automobile (status 1)
    this.carService.getWithStatus(1).subscribe({
      next: (response) => { this.cars = response.data.values as Car[]; },
    });

    this.clientService.getAll().subscribe({
      next: (response) => { this.clients = response.data.values as Client[]; },
    });
  }

  public addRental() {
    if (!this.newRentalForm.valid) {
      alert('Sva polja moraju biti popunjena!');
      return;
    }

    const rental = new Rental();
    rental.startDate = this.newRentalForm.get('startDate')!.value;
    rental.endDate = this.newRentalForm.get('endDate')!.value;
    rental.notes = this.newRentalForm.get('notes')!.value;

    let car: Car = this.newRentalForm.get('car')!.value;
    car.status = 0; // Postavi status na "Not available"

    // Prvo ažuriraj status automobila
    this.carService.updateCar(car).subscribe();

    rental.car = car;
    rental.client = this.newRentalForm.get('client')!.value;
    rental.status = 1; // Admin kreira direktno potvrđen rental
    rental.user = this.userService.getLoggedInUser();

    this.rentalService.addNewRental(rental).subscribe({
      next: (res) => {
        alert('Ugovor o iznajmljivanju je uspešno kreiran!');
        this.router.navigate(['/rentals']);
      },
      error: (err) => {
        alert('Kreiranje ugovora nije uspelo!');
      },
    });
  }
}
