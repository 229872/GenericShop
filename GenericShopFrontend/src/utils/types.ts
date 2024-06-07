export type TokenData = {
  sub: string;
  accountRoles: Role[];
  exp: number,
  lang: string
}

export type Tokens = {
  token: string
  refreshToken: string
}

export type AuthLogs = {
  lastSuccessfulAuthIpAddr: string,
  lastUnsuccessfulAuthIpAddr: string,
  lastSuccessfulAuthTime: Date,
  lastUnsuccessfulAuthTime: Date,
  unsuccessfulAuthCounter: number,
  blockadeEndTime: Date
}

export type Address = {
  postalCode: string,
  country: string,
  city: string,
  street: string,
  houseNumber: number
}

export type Account = {
  id: number,
  version: string,
  archival: boolean,
  login: string,
  email: string,
  locale: string,
  firstName: string,
  lastName: string,
  address: Address,
  accountState: string,
  accountRoles: string[],
  authLogs: AuthLogs
}

export type BasicAccount = {
  id: number
  archival: boolean
  login: string
  email: string
  firstName: string
  lastName: string
  accountState: string
  accountRoles: Role[]
}

export type GridItemData = {
  label: string
  content: string | number
}

export enum Role {
  GUEST = 'GUEST',
  CLIENT = 'CLIENT',
  ADMIN = 'ADMIN',
  EMPLOYEE = 'EMPLOYEE'
}

export type Column<T> = {
  name: string
  dataProp: keyof T
}