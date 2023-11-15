import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {TokenService} from "./token.service";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {Tokens} from "../types/Tokens";
import {NavigationService} from "./navigation.service";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private expiredSessionWarning: number | undefined;

  constructor(
    private httpClient: HttpClient,
    private tokenService: TokenService,
    private navigationService: NavigationService
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

  public showWarningIfSessionExpired(): void {
    const now = Date.now() / 1000;
    this.expiredSessionWarning = setTimeout(() => {
      console.log(now);
    }, (Number(this.tokenService.getExpirationTime()) - now) * 1000);
  }
}
