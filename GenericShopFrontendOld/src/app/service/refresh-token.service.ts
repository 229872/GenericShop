import { Injectable } from '@angular/core';
import {AuthenticationService} from "./authentication.service";
import {TokenService} from "./token.service";
import {DialogService} from "./dialog.service";
import {NavigationService} from "./navigation.service";
import {TranslateService} from "@ngx-translate/core";
import {first} from "rxjs";
import {Tokens} from "../types/Tokens";
import {AlertService} from "@full-fledged/alerts";

@Injectable({
  providedIn: 'root'
})
export class RefreshTokenService {

  constructor(
    private authenticationService: AuthenticationService,
    private tokenService: TokenService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private translateService: TranslateService,
    private alertService: AlertService
  ) { }

  public generateNewToken(): void {
    this.translateService
      .get("dialog.refresh.token")
      .pipe()
      .subscribe(msg => {
        const ref = this.dialogService.openConfirmationDialog(msg, "primary");
        ref.afterClosed()
          .pipe(first())
          .subscribe(result => {
            if (result === 'action') {
              if (!this.tokenService.isTokenExpired()) {
                this.authenticationService.extendSession(this.tokenService.getRefreshToken()!)
                  .pipe(first())
                  .subscribe((tokens: Tokens) => {
                    this.authenticationService.clearExpiredSessionWarning();
                    this.tokenService.saveJwtToken(tokens.token);
                    this.tokenService.saveTimeout(this.tokenService.getRefreshTokenTime()!);
                    this.tokenService.setTimeout(() => {
                      this.generateNewToken();
                    }, this.tokenService.getRefreshTokenTime()!);
                })
              } else {
                this.displayTokenExpiredWarning();
                this.authenticationService.logout();
                void this.navigationService.redirectToAuthenticationPage();
              }
            }
          })
      })
  }

  private displayTokenExpiredWarning(): void {
    this.alertService.warning(this.translateService.instant('auth.token.expired'));
  }
}
