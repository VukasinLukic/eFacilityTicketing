import { Component, OnInit } from '@angular/core';
import { Rental } from '../../../models/rental.model';
import { RentalService } from '../../../services/rental.service';
import { CarService } from '../../../services/car.service';
import { UserService} from '../../../services/user.service';

@Component({
  selector: 'app-rental-list',
  standalone: false,
  templateUrl: './rental-list.component.html',
  styleUrls: ['./rental-list.component.css']
})
export class RentalListComponent implements OnInit {
  public rentals: Rental[] = [];

  constructor(
    private rentalService: RentalService,
    private carService: CarService,
    public userService: UserService,
  ) {}

  ngOnInit() {
    // Status 1 = Confirmed
    this.rentalService.getRentalStatus(1).subscribe({
      next: (response) => {
        this.rentals = response.data.values as Rental[];
      },
    });
  }

  delete(rental: Rental) {
    let car = rental.car;
    car.status = 1; // Vraćamo status auta na "Available"

    this.carService.updateCar(car).subscribe();

    this.rentalService.deleteRental(rental.rentalId).subscribe({
      next: (res) => {
        alert('Iznajmljivanje je uspešno obrisano!');
        this.rentals = this.rentals.filter(r => r.rentalId !== rental.rentalId);
      },
      error: (err) => {
        alert('Brisanje nije uspelo!');
      },
    });
  }
}
