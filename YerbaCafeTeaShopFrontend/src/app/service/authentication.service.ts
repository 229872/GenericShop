import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {TokenService} from "./token.service";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {Tokens} from "../types/Tokens";
import {NavigationService} from "./navigation.service";
import {DialogService} from "./dialog.service";
import {TranslateService} from "@ngx-translate/core";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private expiredSessionWarning: number | undefined;

  constructor(
    private httpClient: HttpClient,
    private tokenService: TokenService,
    private navigationService: NavigationService,
    private dialogService: DialogService,
    private translateService: TranslateService
  ) { }

  public login(login: string, password: string): Observable<Tokens> {
    return this.httpClient.post<Tokens>(`${environment.apiBaseUrl}/auth`, {
      login: login,
      password: password
    });
  }

  public extendSession(refreshToken: string): Observable<Tokens> {
    return this.httpClient.get<Tokens>(`${environment.apiBaseUrl}/auth/extend/${refreshToken}`,
      {
        headers: {
          Authorization: `Bearer ${this.tokenService.getToken()}`,
        }
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
      this.dialogService.openErrorDialog(
        this.translateService.instant('session.expired.title'),
        this.translateService.instant('session.expired.message'),
      ).afterClosed()
        .subscribe(() => {
          void this.navigationService.redirectToAuthenticationPage();
        });
    }, (Number(this.tokenService.getExpirationTime()) - now) * 1000);
  }

  public clearExpiredSessionWarning(): void {
    if (this.expiredSessionWarning) {
      clearTimeout(this.expiredSessionWarning);
    }
  }
}
