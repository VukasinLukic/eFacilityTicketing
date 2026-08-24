import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { Client } from '../../../models/client.model';
import { City } from '../../../models/city.model';
import { CityService } from '../../../services/city.service';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-client-update-modal',
  templateUrl: './client-update-modal.component.html',
  standalone: false,
  styleUrls: ['./client-update-modal.component.css']
})
export class ClientUpdateModalComponent implements OnInit {
  @Input() client!: Client;
  @Output() saveClient = new EventEmitter<Client>();
  @Output() closeModal = new EventEmitter<void>();

  public tempClient!: Client;
  public cities: City[] = [];

  constructor(private cityService: CityService) {}

  ngOnInit() {
    this.tempClient = { ...this.client };
    this.cityService.getAllCities().subscribe({
      next: (response) => {
        this.cities = response.data.values as City[];
        // Postavi selektovani grad
        const match = this.cities.find(c => c.id === this.client.city.id);
        if (match) {
          this.tempClient.city = match;
        }
      },
    });
  }

  cancel() {
    this.closeModal.emit();
  }

  save() {
    this.saveClient.emit(this.tempClient);
  }
}
