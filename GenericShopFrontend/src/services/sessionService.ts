import { environment } from "../utils/constants";
import { getExpirationTime, getJwtToken } from "./tokenService";

export const isTokenExpired = (): boolean => {
  const expirationTime: number | null = getExpirationTime(getJwtToken())

  if (expirationTime === null) {
    return true;
  }

  return expirationTime < Date.now() / 1000;
}

export const isUserSignIn = (): boolean => {
  return !isTokenExpired();
}

export const logout = (): void => {
  localStorage.removeItem(environment.jwtTokenKey);
  localStorage.removeItem(environment.localeKey);
  localStorage.removeItem(environment.refreshTokenKey);
}

export const calculateSessionExpiredTimeout = () => {
  const now = Date.now() / 1000;
  return (Number(getExpirationTime(getJwtToken())) - now) * 1000;
};

export const calculateExtendSessionDialogTimeout = (): number | undefined => {
  const expirationTime = getExpirationTime(getJwtToken());
  if (expirationTime) {
    const sessionTmeInMillis = expirationTime * 1000 - Date.now();

    if (sessionTmeInMillis <= 1.5 * 180 * 1000) {
      return sessionTmeInMillis - (0.3 * 180 * 1000)
    }

    return sessionTmeInMillis - (180 * 1000)
  }
  return undefined;
}