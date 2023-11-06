import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {TokenService} from "./token.service";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {Tokens} from "../types/Tokens";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(
    private httpClient: HttpClient,
    private tokenService: TokenService
  ) { }

  public login(login: string, password: string): Observable<Tokens> {
    return this.httpClient.post<Tokens>(`${environment.apiBaseUrl}/auth`, {
      login: login,
      password: password
    });
  }

  public logout(): void {
    this.tokenService.logout();
  }

  public isUserLoggedIn(): boolean {
    return !this.tokenService.isTokenExpired();
  }
}
