import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Car } from '../../../models/car.model';
import { Rental } from '../../../models/rental.model';
import { CarService } from '../../../services/car.service';
import { UserService } from '../../../services/user.service';
import { ClientService } from '../../../services/client.service';
import { RentalService } from '../../../services/rental.service';
import {CarUpdateModalComponent} from '../car-update-modal/car-update-modal.component';
import {RentalDateSelection} from '../../rentals/rental-modal/rental-modal.component';

@Component({
  selector: 'app-car-card',
  templateUrl: './car-card.component.html',
  standalone: false,
  styleUrls: ['./car-card.component.css']
})
export class CarCardComponent {
  @Input() car!: Car;
  @Output() removedCar = new EventEmitter<Car>();

  showRentalModal = false;
  edit: boolean = false;

  constructor(
    public userService: UserService,
    public clientService: ClientService,
    private carService: CarService,
    public rentalService: RentalService
  ) {}
  openRentalModal() {
    console.log('Dugme "Iznajmi" je kliknuto!');
    this.showRentalModal = true;
  }
  closeModal() {
    this.showRentalModal = false;
  }
  handleDateSelection(selection: RentalDateSelection) {
    const rental = new Rental();
    rental.status = 0; // Pending request
    rental.client = this.clientService.getSpecificClient();
    rental.car = this.car; // 'this.car' je @Input ove komponente

    // Koristimo datume iz modala
    rental.startDate = selection.startDate;
    rental.endDate = selection.endDate;
    rental.notes = 'Zahtev poslat online.';

    this.rentalService.addNewRental(rental).subscribe({
      next: (res) => {
        alert('Zahtev za iznajmljivanje je uspešno poslat!');
        this.closeModal(); // Zatvori modal nakon uspešnog slanja
      },
      error: (err) => {
        alert('Slanje zahteva nije uspelo!');
      },
    });
  }
  onSave(updatedCar: Car) {
    this.carService.updateCar(updatedCar).subscribe({
      next: (res) => {
        this.car = updatedCar;
        this.edit = false;
        alert('Uspešno ažuriran automobil!');
      },
      error: (err) => {
        console.error(err);
        alert('Ažuriranje automobila nije uspelo!');
      },
    });
  }

  delete(car: Car) {
    this.removedCar.emit(car);
  }

  update() {
    this.edit = true;
  }

  rentCar(car: Car) {
    const rental = new Rental();
    rental.status = 0; // 0 = Pending request
    rental.client = this.clientService.getSpecificClient();
    rental.car = car;
    rental.notes = 'Zahtev za iznajmljivanje kreiran.';
    rental.startDate = new Date(); // Ovo bi trebalo da bude forma
    rental.endDate = new Date();   // I ovo bi trebalo da bude forma

    this.rentalService.addNewRental(rental).subscribe({
      next: (res) => {
        alert('Zahtev za iznajmljivanje je uspešno poslat!');
      },
      error: (err) => {
        alert('Slanje zahteva nije uspelo!');
      },
    });
  }
}
