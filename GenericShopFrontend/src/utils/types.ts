export type TokenData = {
  sub: string;
  roles: string[];
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
  state: string,
  roles: string[],
  authLogs: AuthLogs
}

export type GridItemData = {
  label: string
  content: string | number
}