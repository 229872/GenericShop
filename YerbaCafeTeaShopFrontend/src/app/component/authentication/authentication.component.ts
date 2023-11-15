import { Component } from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {AuthenticationService} from "../../service/authentication.service";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {TokenService} from "../../service/token.service";
import {TranslateService} from "@ngx-translate/core";
import {NavigationService} from "../../service/navigation.service";
import {Tokens} from "../../types/Tokens";
import {HttpErrorResponse} from "@angular/common/http";
import {RefreshTokenService} from "../../service/refresh-token.service";
import {AlertService} from "@full-fledged/alerts";

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
    private refreshTokenService: RefreshTokenService,
    private translateService: TranslateService,
    private navigationService: NavigationService,
    private alertService: AlertService
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
          this.tokenService.saveRefreshToken(tokens.refreshToken);
          this.tokenService.saveTimeout(this.tokenService.getRefreshTokenTime()!);
          let accountLanguage: string | undefined = this.tokenService.getTokenData()?.lang;

          if (accountLanguage != undefined) {
            this.tokenService.saveLocale(accountLanguage);
            this.translateService.use(accountLanguage);
          }

          this.tokenService.setTimeout(() => {
            this.refreshTokenService.generateNewToken();
          }, this.tokenService.getRefreshTokenTime()!);

          this.authenticationService.showWarningIfSessionExpired();

          this.translateService.get("authorization.success")
            .pipe(takeUntil(this.destroy))
            .subscribe(msg => {
              this.alertService.success(msg);
              void this.navigationService.redirectToHomePage();
            });
        },
        error: (e: HttpErrorResponse) => {
          combineLatest([
            this.translateService.get('exception.occurred'),
            this.translateService.get(e.error.message || 'exception.unknown')
          ]).pipe(first(), takeUntil(this.destroy), map(data => ({
            title: data[0],
            message: data[1]
          }))).subscribe(data => {
                this.alertService.warning(`${data.title}: ${data.message}`);
              });
        }
      })
  }
}
