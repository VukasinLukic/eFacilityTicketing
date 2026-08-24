import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Client } from '../../../models/client.model';
import { ClientService } from '../../../services/client.service';
import {ClientUpdateModalComponent} from '../client-update-modal/client-update-modal.component';

@Component({
  selector: 'app-client-card',
  standalone: false,
  templateUrl: './client-card.component.html',

  styleUrls: ['./client-card.component.css']
})
export class ClientCardComponent {
  @Input() client!: Client;
  @Output() removedClient = new EventEmitter<Client>();

  edit: boolean = false;

  constructor(private clientService: ClientService) {}

  update() {
    this.edit = true;
  }

  delete(client: Client) {
    this.clientService.deleteClient(client.jmbg).subscribe({
      next: (res: any) => {
        alert('Klijent uspešno obrisan!');
        this.removedClient.emit(client);
      },
      error: (err) => {
        alert('Brisanje klijenta nije uspelo!');
      },
    });
  }

  onSave(updatedClient: Client) {
    this.clientService.updateClient(updatedClient).subscribe({
      next: (res: any) => {
        this.client = updatedClient;
        this.edit = false;
        alert('Podaci o klijentu su uspešno ažurirani!');
      },
      error: (err) => {
        alert('Ažuriranje podataka nije uspelo!');
      },
    });
  }
}
