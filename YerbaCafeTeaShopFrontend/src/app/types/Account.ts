import {Address} from "./Address";

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
  roles: string[]
}
