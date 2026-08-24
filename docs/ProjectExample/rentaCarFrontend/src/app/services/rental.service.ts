import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { Rental } from '../models/rental.model';
import { ClientService } from './client.service';

@Injectable({
  providedIn: 'root',
})
export class RentalService {
  constructor(private http: HttpClient, private clientService: ClientService) {}

  public getAll(): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/rentals/all`);
  }

  public getRentalStatus(status: number): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/rentals/getRentalStatus/${status}`);
  }

  public getRental(id: number): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/rentals/getRental/${id}`);
  }

  public deleteRental(id: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/rentals/delete`, id);
  }

  public addNewRental(rental: Rental): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/rentals/add`, rental);
  }

  public updateRental(rental: Rental): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/rentals/update`, rental);
  }

  public getClientsRentals(): Observable<any> {
    return this.http.post<any>(
      `${environment.apiUrl}/rentals/getClientsRentals`,
      this.clientService.getSpecificClient()
    );
  }
}
