import {Component, OnInit} from '@angular/core';
import {first, Subject, takeUntil} from "rxjs";
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../../service/account.service";
import {TranslateService} from "@ngx-translate/core";
import {DialogService} from "../../service/dialog.service";
import {NavigationService} from "../../service/navigation.service";
import {MatDialogRef} from "@angular/material/dialog";
import {ChangePassword} from "../../types/ChangePassword";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-change-own-password',
  templateUrl: './change-own-password.component.html',
  styleUrls: ['./change-own-password.component.css']
})
export class ChangeOwnPasswordComponent {
  destroy = new Subject<boolean>();
  hide = true;
  currentPassword: string = '';
  newPassword: string = '';
  changePasswordForm: FormGroup;
  passwordMaxLength: number = 32;
  passwordMinLength: number = 8;

  constructor(
    private accountService: AccountService,
    private translateService: TranslateService,
    private dialogService: DialogService,
    private navigationService: NavigationService,
    private dialogRef: MatDialogRef<ChangeOwnPasswordComponent>
  ) {
    this.changePasswordForm = new FormGroup({
      currentPassword: new FormControl(
        '',
        Validators.compose([
          Validators.minLength(8),
          Validators.maxLength(32),
          Validators.pattern('^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$')
        ])),
      newPassword: new FormControl(
        '',
        Validators.compose([
          Validators.minLength(8),
          Validators.maxLength(32),
          Validators.pattern('^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$')
        ])),
      confirmPassword: new FormControl('', Validators.compose([]))
    }, {
      validators: Validators.compose( [
        ChangeOwnPasswordComponent.MatchNewPasswords,
        ChangeOwnPasswordComponent.PasswordsMatch,
        Validators.required
      ]),
    });
    this.changePasswordForm.get('newPassword')?.valueChanges.subscribe(() => {
      this.changePasswordForm.get('confirmPassword')?.updateValueAndValidity();
    });
    this.changePasswordForm.get('currentPassword')?.valueChanges.subscribe(() => {
      this.changePasswordForm.get('newPassword')?.updateValueAndValidity();
    });
  }

  onBackClicked(): void {
    this.dialogRef.close("back");
  }

  changePassword(): void {
    const newPassword: ChangePassword = {
      newPassword: this.changePasswordForm.value['newPassword']!,
      currentPassword: this.changePasswordForm.value['currentPassword']!
    };
    this.accountService
      .changePassword(newPassword)
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: () => {
          this.translateService
            .get('change.password.success')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) =>{
              console.log("Success")
              this.dialogRef.close();
            })
        },
        error: (e: HttpErrorResponse) => {
          this.translateService
            .get(e.error.message || 'exception.current.password.invalid')
            .pipe(takeUntil(this.destroy))
            .subscribe((msg) => {
              console.error(msg)
            });
          if (e.status == 409) {
            this.changePasswordForm.get('newPassword')?.setErrors({ incorrect: true });
            this.changePasswordForm.get('confirmPassword')?.setErrors({ incorrect: true });
          } else if (e.status == 400) {
            this.changePasswordForm.get('currentPassword')?.setErrors({ incorrect: true });
          }
        }
      })
  }

  onPasswordChangeClicked(): void {
    if (this.changePasswordForm.valid) {
      this.translateService
        .get('dialog.change.password.message')
        .pipe(takeUntil(this.destroy))
        .subscribe((msg) => {
          const ref = this.dialogService.openConfirmationDialog(msg, 'primary');
          ref
            .afterClosed()
            .pipe(first(), takeUntil(this.destroy))
            .subscribe((result) => {
              if (result === 'action') {
                this.changePassword();
              }
            });
        });
    } else {
      this.changePasswordForm.markAllAsTouched();
    }
  }

  static MatchNewPasswords(control: AbstractControl) {
    const newPassword = control.get('newPassword')?.value;
    const confirmNewPassword = control.get('confirmPassword')?.value;

    if (newPassword !== confirmNewPassword) {
      control.get('confirmPassword')?.setErrors({ notmatching: true });
      return { notmatching: true };
    } else {
      return null;
    }
  }

  static PasswordsMatch(control: AbstractControl) {
    const password = control.get('currentPassword')?.value;
    const newPassword = control.get('newPassword')?.value;

    if (password === newPassword) {
      control.get('newPassword')?.setErrors({ matching: true });
      return { matching: true };
    } else {
      return null;
    }
  }
}
