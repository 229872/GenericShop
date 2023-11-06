import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {TokenData} from "../types/TokenData";
import {jwtDecode, JwtPayload} from "jwt-decode";

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

  public getToken(): string | null {
    return localStorage.getItem(environment.jwtToken);

  }

  public saveTimeout(timeout: number): void {
    localStorage.setItem(environment.timeoutKey, (Date.now() + timeout).toString());
  }

  public getTimeout(): string | null {
    return localStorage.getItem(environment.timeoutKey);
  }

  public getExpirationTime(): number | null {
    return this.getTokenData()?.exp ?? null;
  }

  public isTokenExpired(): boolean {
    const expirationTime: number | null = this.getExpirationTime();

    if (expirationTime === null) {
      return true;
    }
    return expirationTime < Date.now() / 1000;
  }

  public getTokenData(): TokenData | null {
    const token: string | null = this.getToken();
    if (token === null) {
      return null;
    }

    try {
      const decodedJwtToken: any = jwtDecode(token);
      if (!decodedJwtToken.sub || !decodedJwtToken.roles) {
        return null;
      }

      return {
        sub: decodedJwtToken.sub,
        roles: decodedJwtToken.roles,
        exp: decodedJwtToken.exp
      }
    } catch (e) {
      return null;
    }
  }
}
