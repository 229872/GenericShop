import { Component } from '@angular/core';
import {NavigationService} from "../../service/navigation.service";
import {AuthenticationService} from "../../service/authentication.service";
import {TranslateService} from "@ngx-translate/core";
import {AccountService} from "../../service/account.service";
import {first, Subject, takeUntil} from "rxjs";
import {MatDialogRef} from "@angular/material/dialog";
import {ConfirmationDialogComponent} from "../confirmation-dialog/confirmation-dialog.component";
import {DialogService} from "../../service/dialog.service";
import {environment} from "../../../environments/environment";
import {ErrorDialogComponent} from "../error-dialog/error-dialog.component";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {

  destroy = new Subject<boolean>();

  private locale: string = '';

  constructor(
    private navigationService: NavigationService,
    private authenticationService: AuthenticationService,
    private translateService: TranslateService,
    private dialogService: DialogService,
    private accountService: AccountService
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

  redirectToAccountPage() {

  }

  logout() {
    this.authenticationService.logout();
  }

  getCurrentLocale(): string {
    return this.translateService.currentLang;
  }

  changeLocaleToEnglish(): void {
    this.changeLocale('en');
  }

  changeLocaleToPolish() {
    this.changeLocale('pl');
  }

  changeLocale(lang: string) {
    this.translateService.get('account.edit.locale')
      .pipe(takeUntil(this.destroy))
      .subscribe(msg => {
        const ref: MatDialogRef<ConfirmationDialogComponent> = this.dialogService.openConfirmationDialog(msg, 'primary');
        ref.afterClosed()
          .pipe(first(), takeUntil(this.destroy))
          .subscribe(result => {
            if (result === 'action') {
              this.locale = lang;
              let newLocale = this.locale;

              this.accountService.changeLocale(newLocale)
                .pipe(first(), takeUntil(this.destroy))
                .subscribe({
                  next: () => {
                    localStorage.setItem(environment.localeKey, lang);
                    this.translateService.use(lang);
                    this.translateService.get('account.change.locale.success')
                      .pipe(takeUntil(this.destroy))
                      .subscribe(msg => {
                        this.navigationService.redirectToCurrentPage();
                        console.log(msg)
                      })
                  },
                  error: err => {
                    const title = this.translateService.instant('exception.occurred');
                    const message = this.translateService.instant(err.error.message || 'exception.unknown');
                    const ref: MatDialogRef<ErrorDialogComponent> = this.dialogService.openErrorDialog(title, message);
                    ref.afterClosed()
                      .pipe(first(), takeUntil(this.destroy))
                      .subscribe(() => {
                        this.navigationService.redirectToHomePage();
                      })
                  }
                })
            }
          })
      })
  }
}
