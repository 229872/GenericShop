export const environment = {
  production: false,
  apiBaseUrl: "http://localhost:8080/api/v1",
  jwtTokenKey: "jwtToken",
  localeKey: "locale",
  activeRole: "activeRole",
  lastActiveRole: "lastActiveRole",
  refreshTokenKey: "refreshToken",
  supportedLanguages: ['pl', 'en'] as readonly string[],
  preferences: "preferences",
  defaultLanguage: 'en'
}

export const regex = {
  CAPITALIZED : /^[A-ZĄĆĘŁŃÓŚŻŹ][A-ZĄĆĘŁŃÓŚŻŹa-ząćęłńóśżź]+$/,
  POSTAL_CODE : /^\d{2}-\d{3}$/,
  LOGIN : /^[a-zA-Z][a-zA-Z0-9]+$/,
  PASSWORD : /^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$/,
  TABLE_NAME : /^[a-zA-Z]{1,99}[a-zA-Z](?<![sS])$/
}