import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CityService {
  private readonly serverUrl = `${environment.apiUrl}/cities`;

  constructor(private http: HttpClient) { }

  public getAllCities(): Observable<any> {
    return this.http.get<any>(`${this.serverUrl}/all`);
  }
}
