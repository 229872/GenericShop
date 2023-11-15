import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {TokenData} from "../types/TokenData";
import {jwtDecode, JwtPayload} from "jwt-decode";

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private timeoutId: any;
  constructor() { }

  public logout(): void {
    localStorage.removeItem(environment.jwtTokenKey);
    localStorage.removeItem(environment.localeKey);
    localStorage.removeItem(environment.refreshTokenKey);
    localStorage.removeItem(environment.timeoutKey);
    clearTimeout(this.timeoutId);
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
      let sessionTimeInMillis = expirationTime * 1000 - Date.now();

      if (sessionTimeInMillis <= 1.5 * TokenService.REFRESH_TOKEN_TIME_IN_SECONDS * 1000) {
        return sessionTimeInMillis - (0.3 * TokenService.REFRESH_TOKEN_TIME_IN_SECONDS * 1000);
      }

      return sessionTimeInMillis - (TokenService.REFRESH_TOKEN_TIME_IN_SECONDS * 1000);
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

  public setTimeout(callback: () => void, delay: number) {
    this.timeoutId = setTimeout(callback, delay);
  }

  public static readonly REFRESH_TOKEN_TIME_IN_SECONDS = 180;
}
