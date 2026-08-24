import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { Client } from '../models/client.model';

@Injectable({
  providedIn: 'root',
})
export class ClientService {
  private currentClient!: Client;

  constructor(private http: HttpClient) {}

  public getAll(): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/clients/all`);
  }

  public addNewClient(client: Client): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/clients/add`, client);
  }

  public deleteClient(jmbg: string): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/clients/delete`, jmbg);
  }

  public login(username: string, password: string): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/clients/login/${username}/${password}`);
  }

  public searchClients(search: string): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/clients/search/${search}`);
  }

  public updateClient(client: Client): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/clients/update`, client);
  }

  public getClient(jmbg: string): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/clients/getClient/${jmbg}`);
  }

  // Metode za rad sa sesijom
  public getUserStatus() {
    return sessionStorage.getItem('currentUser');
  }
  public setUserStatus() {
    sessionStorage.setItem('currentUser', 'user');
  }

  public setClient(client: Client) {
    this.currentClient = client;
  }

  public getSpecificClient() {
    return this.currentClient;
  }

  public setSessionData(res: any, client: Client) {
    sessionStorage.setItem('user', String(res.username));
    this.setUserStatus();
    this.setClient(client);
  }

  public logOut() {
    sessionStorage.clear();
    this.currentClient = new Client();
  }
}
