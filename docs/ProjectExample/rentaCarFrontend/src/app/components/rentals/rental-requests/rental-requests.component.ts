import { Component, OnInit } from '@angular/core';
import { Rental } from '../../../models/rental.model';
import { RentalService } from '../../../services/rental.service';
import { UserService } from '../../../services/user.service';
import { ClientService } from '../../../services/client.service';
import { CarService } from '../../../services/car.service';

@Component({
  selector: 'app-rental-requests',
  templateUrl: './rental-requests.component.html',
  standalone: false,
  styleUrls: ['./rental-requests.component.css']
})
export class RentalRequestsComponent implements OnInit {
  public pendingRentals: Rental[] = [];      // Zahtevi koje admin vidi
  public clientSpecificRentals: Rental[] = []; // Zahtevi koje klijent vidi

  constructor(
    private rentalService: RentalService,
    public userService: UserService,
    public clientService: ClientService,
    private carService: CarService
  ) {}

  ngOnInit() {
    // Admin vidi sve zahteve sa statusom 0 (Pending)
    this.rentalService.getRentalStatus(0).subscribe({
      next: (response) => {
        this.pendingRentals = response.data.values as Rental[];
      },
    });

    // Klijent vidi samo svoje zahteve
    this.rentalService.getClientsRentals().subscribe((res) => {
      this.clientSpecificRentals = res.data.values as Rental[];
    });
  }

  accept(rental: Rental) {
    let car = rental.car;
    car.status = 0; // Postavi status auta na "Not available"

    // 1. Ažuriraj auto
    this.carService.updateCar(car).subscribe();

    const currentUser = this.userService.getLoggedInUser();
    if (!currentUser) {
      alert('Nema ulogovanog radnika!');
      return;
    }

    // 2. Ažuriraj rental
    rental.status = 1; // Postavi status na "Confirmed"
    rental.user = currentUser;
    this.rentalService.updateRental(rental).subscribe(() => {
      // Ukloni iz liste zahteva
      this.pendingRentals = this.pendingRentals.filter(r => r.rentalId !== rental.rentalId);

      // Opciono: odbij sve ostale zahteve za isti auto
      this.rejectOtherRequestsForCar(car.id, rental.rentalId);
    });
  }

  decline(rental: Rental) {
    const currentUser = this.userService.getLoggedInUser();
    if (!currentUser) {
      alert('Nema ulogovanog radnika!');
      return;
    }

    rental.status = 2; // Postavi status na "Declined"
    rental.user = currentUser;
    this.rentalService.updateRental(rental).subscribe(() => {
      this.pendingRentals = this.pendingRentals.filter(r => r.rentalId !== rental.rentalId);
    });
  }

  rejectOtherRequestsForCar(carId: number, acceptedRentalId: number){
    this.pendingRentals.forEach(req => {
      if(req.car.id === carId && req.rentalId !== acceptedRentalId){
        this.decline(req);
      }
    });
  }
}
