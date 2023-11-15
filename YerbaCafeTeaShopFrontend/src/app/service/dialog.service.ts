import { Injectable } from '@angular/core';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ConfirmationDialogComponent} from "../component/confirmation-dialog/confirmation-dialog.component";
import {ErrorDialogComponent} from "../component/error-dialog/error-dialog.component";
import {ChangeOwnPasswordComponent} from "../component/change-own-password/change-own-password.component";

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(
    private matDialog: MatDialog
  ) { }

  openConfirmationDialog(message: string, color: string): MatDialogRef<ConfirmationDialogComponent> {
    return this.matDialog.open(ConfirmationDialogComponent, {
      data: {
        message: message,
        color: color
      }
    });
  }

  openErrorDialog(title: string, message: string): MatDialogRef<ErrorDialogComponent> {
    return this.matDialog.open(ErrorDialogComponent,{
      data: {
        title: title,
        message: message
      }
    });
  }

  openChangePasswordDialog(): MatDialogRef<ChangeOwnPasswordComponent> {
    return this.matDialog.open(ChangeOwnPasswordComponent, {
      disableClose: true,
      width: '480px',
      height: '560px',
    });
  }

}