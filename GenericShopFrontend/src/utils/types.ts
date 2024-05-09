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