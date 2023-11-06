import { Component } from '@angular/core';
import {NavigationService} from "../../service/navigation.service";
import {AuthenticationService} from "../../service/authentication.service";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {


  constructor(
    private navigationService: NavigationService,
    private authenticationService: AuthenticationService
  ) {
  }

  redirectToAuthenticationPage() {
    this.navigationService.redirectToAuthenticationPage();
  }

  redirectToHomePage(): void {
    this.navigationService.redirectToHomePage();
  }

  isUserLoggedIn(): boolean {
    return this.authenticationService.isUserLoggedIn();
  }
}
