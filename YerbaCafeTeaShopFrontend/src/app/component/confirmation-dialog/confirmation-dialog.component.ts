import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.css']
})
export class ConfirmationDialogComponent {
  constructor(
    private dialogRef: MatDialogRef<ConfirmationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any
  ) {}

  getMessage(): string {
    return this.data.message;
  }

  getColor(): string {
    return this.data.color;
  }

  onActionClick(): void {
    this.dialogRef.close('action');
  }

  onCancelClick(): void {
    this.dialogRef.close('cancel');
  }
}
