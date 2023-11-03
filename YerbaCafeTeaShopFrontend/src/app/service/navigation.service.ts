import { Injectable } from '@angular/core';
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class NavigationService {

  constructor(
    private router: Router
  ) { }

  public redirectToHomePage(): Promise<boolean> {
    return this.router.navigate(['/home']);
  }

  public redirectToAuthenticationPage(): Promise<boolean> {
    return this.router.navigate(['/login']);
  }
}
