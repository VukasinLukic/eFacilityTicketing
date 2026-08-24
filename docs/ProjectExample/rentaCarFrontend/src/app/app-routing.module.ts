import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { LoginClientComponent } from './auth/login-client/login-client.component';
import { RegisterComponent } from './auth/register/register.component';

import { CarListComponent } from './components/cars/car-list/car-list.component';
import { CarAddComponent } from './components/cars/car-add/car-add.component';

import { ClientListComponent } from './components/clients/client-list/client-list.component';
import { ClientAddComponent } from './components/clients/client-add/client-add.component';

import { RentalListComponent } from './components/rentals/rental-list/rental-list.component';
import { RentalAddComponent } from './components/rentals/rental-add/rental-add.component';
import { RentalRequestsComponent } from './components/rentals/rental-requests/rental-requests.component';


const routes: Routes = [
  //  Autentifikacija
  { path: 'login-admin', component: LoginComponent },
  { path: 'login', component: LoginClientComponent }, // Glavni login je za klijente
  { path: 'register', component: RegisterComponent },

  //  Automobili (Cars)
  { path: 'cars', component: CarListComponent },
  { path: 'cars/add', component: CarAddComponent }, // Ruta za dodavanje novog auta

  //  Klijenti (Clients)
  { path: 'clients', component: ClientListComponent },
  { path: 'clients/add', component: ClientAddComponent }, // Ruta za dodavanje novog klijenta

  //  Iznajmljivanja (Rentals)
  { path: 'rentals', component: RentalListComponent }, // Prikazuje potvrđena iznajmljivanja
  { path: 'rentals/add', component: RentalAddComponent }, // Forma za admina da doda novi ugovor
  { path: 'rentals/requests', component: RentalRequestsComponent }, // Prikazuje zahteve (i za admina i za klijenta)

  //  Podrazumevana ruta
  // Ako korisnik unese praznu putanju, preusmeri ga na listu automobila
  { path: '', redirectTo: '/cars', pathMatch: 'full' },

  //  Wildcard ruta (za nepostojeće stranice)
  // Ako korisnik unese URL koji ne postoji, takođe ga preusmeri na listu automobila
  { path: '**', redirectTo: '/cars' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
