import { NgModule } from '@angular/core';
import {AlertModule} from '@full-fledged/alerts';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NavbarComponent } from './component/navbar/navbar.component';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIconModule} from "@angular/material/icon";
import {MatButtonModule} from "@angular/material/button";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatMenuModule} from "@angular/material/menu";
import { AuthenticationComponent } from './component/authentication/authentication.component';
import { HomeComponent } from './component/home/home.component';
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {HttpClient, HttpClientModule} from "@angular/common/http";
import {TranslateHttpLoader} from "@ngx-translate/http-loader";
import {ReactiveFormsModule} from "@angular/forms";
import {MatCardModule} from "@angular/material/card";
import {MatInputModule} from "@angular/material/input";
import {MatListModule} from "@angular/material/list";
import { ConfirmationDialogComponent } from './component/confirmation-dialog/confirmation-dialog.component';
import { ErrorDialogComponent } from './component/error-dialog/error-dialog.component';
import {MatDialogModule} from "@angular/material/dialog";
import { AccountComponent } from './component/account/account.component';
import {DatePipe, NgOptimizedImage} from "@angular/common";
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ChangeOwnPasswordComponent } from './component/change-own-password/change-own-password.component';


export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http);
}

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    AuthenticationComponent,
    HomeComponent,
    ConfirmationDialogComponent,
    ErrorDialogComponent,
    AccountComponent,
    ChangeOwnPasswordComponent
  ],
  imports: [
    BrowserModule,
    AlertModule.forRoot({maxMessages: 8, timeout: 5000, positionX: 'right', positionY: 'top'}),
    AppRoutingModule,
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    BrowserAnimationsModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatMenuModule,
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatListModule,
    MatDialogModule,
    NgOptimizedImage,
    NgbModule
  ],
  providers: [
    DatePipe
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }


