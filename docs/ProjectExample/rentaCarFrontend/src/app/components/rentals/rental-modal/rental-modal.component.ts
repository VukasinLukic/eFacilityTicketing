import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Car } from '../../../models/car.model';

// Definišemo interfejs za podatke koje vraćamo
export interface RentalDateSelection {
  startDate: Date;
  endDate: Date;
}

@Component({
  selector: 'app-rental-modal',
  standalone: false,
  templateUrl: './rental-modal.component.html',
  styleUrls: ['./rental-modal.component.css']
})
export class RentalModalComponent implements OnInit {
  @Input() car!: Car; // Prima podatke o autu koji se iznajmljuje
  @Output() closeModal = new EventEmitter<void>();
  @Output() datesSelected = new EventEmitter<RentalDateSelection>();

  rentalForm!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.rentalForm = this.fb.group({
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    });
  }

  submitRequest() {
    if (this.rentalForm.invalid) {
      alert('Oba datuma su obavezna.');
      return;
    }
    // Vraćamo odabrane datume roditeljskoj komponenti
    this.datesSelected.emit(this.rentalForm.value);
  }

  cancel() {
    this.closeModal.emit();
  }
}
