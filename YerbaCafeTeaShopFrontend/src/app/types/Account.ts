import {Address} from "./Address";
import {AuthLogs} from "./AuthLogs";

export interface Account {
  id: number,
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
