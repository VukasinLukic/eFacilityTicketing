import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './auth/login/login.component';
import { LoginClientComponent } from './auth/login-client/login-client.component';
import { RegisterComponent } from './auth/register/register.component';
import { CarListComponent } from './components/cars/car-list/car-list.component';
import { CarCardComponent } from './components/cars/car-card/car-card.component';
import { CarAddComponent } from './components/cars/car-add/car-add.component';
import { CarUpdateModalComponent } from './components/cars/car-update-modal/car-update-modal.component';
import { RentalListComponent } from './components/rentals/rental-list/rental-list.component';
import { RentalCardComponent } from './components/rentals/rental-card/rental-card.component';
import { RentalAddComponent } from './components/rentals/rental-add/rental-add.component';
import { RentalRequestsComponent } from './components/rentals/rental-requests/rental-requests.component';
import { ClientListComponent } from './components/clients/client-list/client-list.component';
import { ClientCardComponent } from './components/clients/client-card/client-card.component';
import { ClientAddComponent } from './components/clients/client-add/client-add.component';
import { ClientUpdateModalComponent } from './components/clients/client-update-modal/client-update-modal.component';
import { RentalModalComponent } from './components/rentals/rental-modal/rental-modal.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    LoginClientComponent,
    RegisterComponent,
    CarListComponent,
    CarCardComponent,
    CarAddComponent,
    CarUpdateModalComponent,
    RentalListComponent,
    RentalCardComponent,
    RentalAddComponent,
    RentalRequestsComponent,
    ClientListComponent,
    ClientCardComponent,
    ClientAddComponent,
    ClientUpdateModalComponent,
    RentalModalComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    provideClientHydration(withEventReplay())
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
