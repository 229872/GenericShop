export const environment = {
  production: false,
  apiBaseUrl: "http://localhost:8080/api/v1",
  jwtTokenKey: "jwtToken",
  timeoutKey: "tokenTimeout",
  localeKey: "locale",
  refreshTokenKey: "refreshToken"
}

export const regex = {
  CAPITALIZED : /^[A-ZĄĆĘŁŃÓŚŻŹ][A-ZĄĆĘŁŃÓŚŻŹa-ząćęłńóśżź]+$/,
  POSTAL_CODE : /^\d{2}-\d{3}$/,
  LOGIN : /^[a-zA-Z][a-zA-Z0-9]+$/,
  PASSWORD: /^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$/
}