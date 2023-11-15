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
    localStorage.removeItem(environment.jwtTokenKey);
    localStorage.removeItem(environment.localeKey);
  }

  public saveJwtToken(token: string): void {
    localStorage.setItem(environment.jwtTokenKey, token);
  }

  public saveRefreshToken(token: string): void {
    localStorage.setItem(environment.refreshTokenKey, token);
  }

  public saveLocale(lang: string): void {
    localStorage.setItem(environment.localeKey, lang);
  }

  public getToken(): string | null {
    return localStorage.getItem(environment.jwtTokenKey);

  }

  public getRefreshToken(): string | null {
    return localStorage.getItem(environment.refreshTokenKey);
  }

  public getRefreshTokenTime(): number | null {
    const expirationTime = this.getExpirationTime();
    if (expirationTime !== null) {
      return expirationTime * 1000 - Date.now() - TokenService.REFRESH_TOKEN_TIME;
    }
    return null;
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
        exp: decodedJwtToken.exp,
        lang: decodedJwtToken.lang
      }
    } catch (e) {
      return null;
    }
  }
  public static readonly REFRESH_TOKEN_TIME = 180000;
}
