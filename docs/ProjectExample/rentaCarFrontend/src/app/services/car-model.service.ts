import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CarModelService {
  private readonly serverUrl = `${environment.apiUrl}/car-models`;

  constructor(private http: HttpClient) { }

  public getAllCarModels(): Observable<any> {
    return this.http.get<any>(`${this.serverUrl}/all`);
  }
}
