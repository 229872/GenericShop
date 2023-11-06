import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {Account} from "../types/Account";
import {TokenService} from "./token.service";

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  private accountApi: string = `${environment.apiBaseUrl}/account`;

  constructor(
    private httpClient: HttpClient,
    private tokenService: TokenService
  ) { }

  public getOwnAccount(): Observable<Account> {
    return this.httpClient.get<Account>(`${this.accountApi}/self`, {
      headers: {
        Authorization: `Bearer ${this.tokenService.getToken()}`
      }
    });
  }

  public changeLocale(locale: string): Observable<Account> {
    return this.httpClient.put<Account>(`${this.accountApi}/self/change-locale`, {
      "locale": locale
    }, {
      headers: {
        Authorization: `Bearer ${this.tokenService.getToken()}`
      }
    });
  }

}
