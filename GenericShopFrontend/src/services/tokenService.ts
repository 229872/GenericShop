import { environment } from "../utils/constants";
import { jwtDecode } from "jwt-decode";
import { Role, TokenData } from "../utils/types";


export const saveJwtToken = (token: string): void => {
localStorage.setItem(environment.jwtTokenKey, token)
}

export const saveRefreshToken = (token: string): void => {
  localStorage.setItem(environment.refreshTokenKey, token)
}

export const saveLocale = (lang: string): void => {
  localStorage.setItem(environment.localeKey, lang)
}

export const saveActiveRole = (activeRole: Role): void => {
  localStorage.setItem(environment.activeRole, activeRole)
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

export const getExpirationTime = (token: string | null): number | null => {
  return decodeJwtToken(token)?.exp ?? null;
}

export const getActiveRole = (token: string | null): Role => {
  const activeRole: string | null = localStorage.getItem(environment.activeRole)
  return activeRole === null || activeRole as Role === Role.GUEST ? decodeJwtToken(token)?.accountRoles[0] ?? Role.GUEST : activeRole as Role
}

export const getRoles = (token: string | null): Role[] => {
  return decodeJwtToken(token)?.accountRoles ?? []
}

export const getLogin = (token: string | null): string | null => {
  return decodeJwtToken(token)?.sub ?? null
}

export const decodeJwtToken = (token: string | null): TokenData | null => {
  if (token === null) return null;

  try {
    const decodedJwtToken: TokenData = jwtDecode(token);
    

    return {
      sub: decodedJwtToken.sub,
      accountRoles: decodedJwtToken.accountRoles,
      exp: decodedJwtToken.exp,
      lang: decodedJwtToken.lang
    }

  } catch (e) {
    return null;
  }
} 
