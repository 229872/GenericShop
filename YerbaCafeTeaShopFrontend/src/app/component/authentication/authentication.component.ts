import { Component } from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {AuthenticationService} from "../../service/authentication.service";
import {first, Subject, takeUntil} from "rxjs";
import {TokenService} from "../../service/token.service";
import {TranslateService} from "@ngx-translate/core";
import {NavigationService} from "../../service/navigation.service";
import {Tokens} from "../../types/Tokens";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.css']
})
export class AuthenticationComponent {
  loginForm = new FormGroup({
    login: new FormControl(''),
    password: new FormControl('')
  });
  destroy = new Subject<boolean>();

  hide: boolean = true;
  loginMaxLength: number = 20;
  loginMinLength: number = 6;
  loginPattern: string = "^[a-zA-Z][a-zA-Z0-9]*$";
  passwordMaxLength: number = 32;
  passwordMinLength: number = 8;
  passwordPattern: string = "^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$";

  constructor(
    private authenticationService: AuthenticationService,
    private tokenService: TokenService,
    private translateService: TranslateService,
    private navigationService: NavigationService
  ) {
  }

  onLoginClicked() {
    let login: string = this.loginForm.value['login'] ?? '';
    let password: string = this.loginForm.value['password'] ?? '';

    this.authenticationService.login(login, password)
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: (tokens: Tokens) => {
          this.tokenService.saveJwtToken(tokens.token);

          console.log(tokens.token);
          this.translateService.get("authorization.success")
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.navigationService.redirectToHomePage();
            });
        },
        error: (e: HttpErrorResponse) => {
          console.log(e)
        }
      })
  }
}
