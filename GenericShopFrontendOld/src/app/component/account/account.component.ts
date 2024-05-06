import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {combineLatest, first, map, Subject, takeUntil} from "rxjs";
import {Account} from "../../types/Account";
import {AuthenticationService} from "../../service/authentication.service";
import {DatePipe, formatDate, Location} from "@angular/common";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../service/dialog.service";
import {NavigationService} from "../../service/navigation.service";
import {TokenService} from "../../service/token.service";
import {MatDialog} from "@angular/material/dialog";
import {AccountService} from "../../service/account.service";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit {

  accountForm = new FormGroup({
    login: new FormControl({value: '', disabled: true})
  });
  destroy = new Subject<boolean>();
  account: Account | undefined;
  loading = true;

  constructor(
    private authenticationService: AuthenticationService,
    private datePipe: DatePipe,
    private translateService: TranslateService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private location: Location,
    private tokenService: TokenService,
    private dialog: MatDialog,
    private accountService: AccountService
  ) {
  }

  ngOnInit(): void {
    this.loadAccount();
  }

  loadAccount(): void {
    this.accountService.getOwnAccount()
      .pipe(first(), takeUntil(this.destroy))
      .subscribe({
        next: (account : Account) => {
          this.account = account;
          this.loading = false;
        },
        error: (e : HttpErrorResponse) => {
          combineLatest([
            this.translateService.get('exception.occurred'),
            this.translateService.get(e.error.message || 'exception.unknown')
          ]).pipe(first(), takeUntil(this.destroy), map(data => ({
            title: data[0],
            message: data[1]
          })))
            .subscribe(data => {
              const ref = this.dialogService.openErrorDialog(data.title, data.message);
              ref.afterClosed()
                .pipe(first(), takeUntil(this.destroy))
                .subscribe(() => {
                  this.navigationService.redirectToHomePage();
                })
            });
        }
      })
  }

  getCardStyling(): any {
    return {
      'background-color': document.body.classList.contains('dark-mode') ? '#424242' : '#fafafa'
    };
  }

  formatDate(date: Date | undefined): string {
    return this.datePipe.transform(date, 'yyyy-MM-dd HH:mm:ss') ?? '-';
  }


  reloadData() {

  }

  openEditAccountDialog() {

  }

  openChangeEmailDialog() {

  }

  openChangePasswordDialog() {
    this.dialogService.openChangePasswordDialog();
  }

  getRoles() {
    return this.account?.roles.map(role => this.translateService.instant(`role.${role.toLowerCase()}`)).join(', ') ?? '-';
  }

  getAccountState(): string {
    switch (this.account?.state.toUpperCase()) {
      case 'BLOCKED':
        return 'account.state.blocked';
      case 'ACTIVE':
        return 'account.state.active';
      case 'NOT_VERIFIED':
        return 'account.state.not_verified';
      default: return '-'
    }
  }
}
