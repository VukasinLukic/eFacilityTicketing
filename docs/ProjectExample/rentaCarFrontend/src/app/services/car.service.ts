import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { Car } from '../models/car.model';

@Injectable({
  providedIn: 'root',
})
export class CarService {
  constructor(private http: HttpClient) {}

  public addNewCar(car: Car): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/cars/add`, car);
  }

  public getAll(): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/cars/all`);
  }

  public updateCar(car: Car): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/cars/update`, car);
  }

  public searchCars(search: string): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/cars/search/${search}`);
  }

  public delete(carId: number): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/cars/delete`, carId);
  }

  public getCar(id: number): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/cars/getCar/${id}`);
  }

  public getWithStatus(status: number): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/cars/withStatus/${status}`);
  }
}
