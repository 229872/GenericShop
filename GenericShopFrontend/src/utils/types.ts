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
  accountState: AccountState,
  accountRoles: Role[],
  authLogs: AuthLogs
}

export type BasicAccount = {
  id: number
  archival: boolean
  login: string
  email: string
  firstName: string
  lastName: string
  accountState: AccountState
  accountRoles: Role[]
}

export type BasicProduct = {
  id: number
  orderedProductId?: number
  archival: boolean
  name: string
  price: number
  quantity: number
  imageUrl: string
  averageRating: number
  rate?: number
}

export type BasicProductWithVersion = BasicProduct & {
  version: string
}

export type BasicProductWithFixedPrice = {
  id: number
  archival: boolean
  name: string
  price: string
  quantity: number
  imageUrl: string
  averageRating: number
}

export type ProductData = {
  id: number
  version: string
  archival: boolean
  name: string
  price: number
  quantity: number
  imageUrl: string
  rates: 0 | 1 | 2 | 3 | 4 | 5[]
  categoryProperties: Object
}

export type BasicOrder = {
  id: number
  totalPrice: number
  creationDate: string
}

export type BasicOrderWithFixedPrice = {
  id: number
  totalPrice: string
  creationDate: string
}

export type FullOrder = BasicOrder & {
  accountId: number
  version: string
  products: BasicProduct[]
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

export enum AccountState {
  BLOCKED = 'BLOCKED',
  ACTIVE = 'ACTIVE',
  NOT_VERIFIED = 'NOT_VERIFIED'
}

export enum AuthenticatedAccountState  {
  ACTIVE = 'ACTIVE',
  BLOCKED = 'BLOCKED'
}

export enum AuthenticatedAccountRole {
  CLIENT = 'CLIENT',
  ADMIN = 'ADMIN',
  EMPLOYEE = 'EMPLOYEE'
}

export type Column<T> = {
  name: string
  dataProp: keyof T
  label: boolean
}
