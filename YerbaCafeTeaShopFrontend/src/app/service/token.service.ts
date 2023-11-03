import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  constructor() { }

  public logout(): void {
    localStorage.removeItem(environment.jwtToken)
  }

  public saveJwtToken(token: string): void {
    localStorage.setItem(environment.jwtToken, token);
  }

  public getToken(): string {
    return localStorage.getItem(environment.jwtToken);
  }
}
