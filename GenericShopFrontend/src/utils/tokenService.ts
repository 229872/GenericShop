import { environment } from "./constants";
import { jwtDecode } from "jwt-decode";
import { TokenData } from "./types";


export const saveJwtToken = (token: string): void => {
  localStorage.setItem(environment.jwtTokenKey, token)
}

export const saveRefreshToken = (token: string): void => {
  localStorage.setItem(environment.refreshTokenKey, token)
}

export const saveLocale = (lang: string): void => {
  localStorage.setItem(environment.localeKey, lang)
}

export const saveTimeout = (timeout: number): void => {
  localStorage.setItem(environment.timeoutKey, (Date.now() + timeout).toString())
}



export const getJwtToken = (): string | null => {
  return localStorage.getItem(environment.jwtTokenKey)
}

export const getRefreshToken = (): string | null => {
  return localStorage.getItem(environment.refreshTokenKey)
}

export const getLocale = (): string | null => {
  return localStorage.getItem(environment.localeKey)
}

export const getTimeout = (): string | null => {
  return localStorage.getItem(environment.timeoutKey)
}

export const getExpirationTime = (token: string | null): number | null => {
  return decodeJwtToken(token)?.exp ?? null;
}



export const decodeJwtToken = (token: string | null): TokenData | null => {
  if (token === null) return null;

  try {
    const decodedJwtToken: TokenData = jwtDecode(token);
    

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
