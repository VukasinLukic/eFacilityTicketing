import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { Car } from '../../../models/car.model';
import { CarModel } from '../../../models/car-model.model';
import { CarModelService } from '../../../services/car-model.service';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-car-update-modal',
  standalone: false,
  templateUrl: './car-update-modal.component.html',
  styleUrls: ['./car-update-modal.component.css']
})
export class CarUpdateModalComponent implements OnInit {
  @Input() car!: Car;
  @Output() saveCar = new EventEmitter<Car>();
  @Output() closeModal = new EventEmitter<void>();

  public tempCar!: Car;
  public carModels: CarModel[] = [];

  constructor(
    private carModelService: CarModelService
  ) {}

  ngOnInit() {
    // Kreiramo kopiju da ne bismo menjali original dok se ne klikne 'Save'
    this.tempCar = { ...this.car };

    this.carModelService.getAllCarModels().subscribe({
      next: (res) => {
        this.carModels = res.data.values as CarModel[];
        // Postavljamo selektovani model u dropdown-u
        const match = this.carModels.find(m => m.id === this.car.carModel.id);
        if (match) {
          this.tempCar.carModel = match;
        }
      },
      error: (err) => console.error(err)
    });
  }

  save() {
    // Opciono: Dodati validaciju kao u originalnom projektu
    this.saveCar.emit(this.tempCar);
  }

  cancel() {
    this.closeModal.emit();
  }
}
