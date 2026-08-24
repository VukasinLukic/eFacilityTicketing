import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { Response } from '../connection/response';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  public login(username: String, password: String): Observable<Response> {
    return this.http.get<Response>(
      environment.apiUrl + '/users/login/' + username + '/' + password
    );
  }

  public setSessionData(res: any) {
    if (!res.id) {
      console.error('Login response missing user ID:', res);
      throw new Error('User ID missing in login response');
    }
    //sessionStorage.setItem('user', String(res.username));
    const user = {
      id: res.id,
      name: res.name,
      surname: res.surname,
      username: res.username,
      password: ''
    };
    sessionStorage.setItem('user', JSON.stringify(user));
    sessionStorage.setItem('userId', String(res.id));
    this.setUserStatus();
  }
  public getLoggedInUser(): User  | null {
    // const userStr = sessionStorage.getItem('user');
    // return userStr ? JSON.parse(userStr) : null;
    const userStr = sessionStorage.getItem('user');
    if (!userStr) return null;

    try {
      return JSON.parse(userStr) as User;
    } catch (err) {
      console.error('Failed to parse user from sessionStorage', err);
      return null;
    }
  }
  public getUserStatus() {
    return sessionStorage.getItem('currentUser');
  }

  public setUserStatus() {
    sessionStorage.setItem('currentUser', 'admin');
  }

  public logOut() {
    sessionStorage.clear();
  }
  public getUserId(): number {
    return Number(sessionStorage.getItem('userId') || 0);
  }
}
