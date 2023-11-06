import { Injectable } from '@angular/core';
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class NavigationService {

  constructor(
    private router: Router
  ) { }

  public redirectToCurrentPage(): Promise<boolean> {
    return this.router.navigateByUrl(this.router.url);
  }

  public redirectToHomePage(): Promise<boolean> {
    return this.router.navigate(['/home']);
  }

  public redirectToAuthenticationPage(): Promise<boolean> {
    return this.router.navigate(['/login']);
  }
}
