export interface AuthLogs {
  lastSuccessfulAuthIpAddr: string,
  lastUnsuccessfulAuthIpAddr: string,
  lastSuccessfulAuthTime: Date,
  lastUnsuccessfulAuthTime: Date,
  unsuccessfulAuthCounter: number,
  blockadeEndTime: Date
}
