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

export type SessionDialogsActions = {
  showTokenExpiredDialogAfterTimeout: () => void
  showExtendSessionDialogAfterTimeout: () => void
  setLoading: (value: boolean) => void
}