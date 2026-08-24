import { Component, OnInit } from '@angular/core';
import { Client } from '../../../models/client.model';
import { ClientService } from '../../../services/client.service';
import {ClientCardComponent} from '../client-card/client-card.component';
import {UserService} from '../../../services/user.service';

@Component({
  selector: 'app-client-list',
  templateUrl: './client-list.component.html',
  standalone: false,
  styleUrls: ['./client-list.component.css']
})
export class ClientListComponent implements OnInit {
  public clients: Client[] = [];
  public filteredClients: Client[] = [];

  constructor(private clientService: ClientService,
  public UserService: UserService,) {}

  ngOnInit() {
    this.clientService.getAll().subscribe({
      next: (response) => {
        this.clients = response.data.values as Client[];
        this.filteredClients = this.clients;
      },
    });
  }

  onSearch(search: string) {
    if (!search) {
      this.filteredClients = this.clients;
    } else {
      this.clientService.searchClients(search).subscribe((res) => {
        this.filteredClients = res.data.values as Client[];
      });
    }
  }

  deleteClient(c: Client) {
    this.clientService.deleteClient(c.jmbg).subscribe((res) => {
      alert(res.message);
      this.clients = this.clients.filter(client => client.jmbg !== c.jmbg);
      this.filteredClients = this.clients;
    });
  }
}
