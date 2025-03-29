package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.ChangeEmailDialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.ChangePasswordDialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.config.Resources;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.AccountOutputDto;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.AddressDto;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.AuthLogOutputDto;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.ChangePasswordDto;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.Services;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.ERROR;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.INFO;

class AccountSubSceneController implements Controller, Initializable {

    private final HttpService httpService;
    private final AnimationService animationService;
    private final ResourceBundle bundle;


    AccountSubSceneController(Services services, ResourceBundle bundle) {
        requireNonNull(services);
        this.bundle = requireNonNull(bundle);
        this.httpService = services.http();
        this.animationService = services.animation();
    }


    @FXML
    private Label labelLogin, labelEmail, labelLocale, labelFirstName, labelLastName, labelAccountState, labelAccountRoles;

    @FXML
    private Label labelStreet, labelHouseNumber, labelCity, labelPostalCode, labelCountry;

    @FXML
    private Label labelLastSuccessIp, labelLastUnsuccessIp, labelLastSuccessTime, labelLastUnsuccessTime;

    @FXML
    private Label labelUnsuccessCounter, labelBlockadeEnd;

    @FXML
    private Button buttonEdit, buttonChangePassword, buttonChangeEmail;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpAccountData(resourceBundle);
        setUpButtons();
    }


    private void setUpAccountData(ResourceBundle bundle) {
        try {
            AccountOutputDto account = httpService.sendGetOwnAccountInformationRequest();

            setUpAccountLabels(account);
            setUpAddressLabels(requireNonNull(account.address()));
            setUpAuthLogsLabels(requireNonNull(account.authLogs()));

        } catch (ApplicationException e) {
            e.printStackTrace();

            Dialog.builder()
                .type(ERROR)
                .title(bundle.getString("error.title"))
                .text(bundle.getString("error.text"))
                .display();
        }
    }


    private void setUpAccountLabels(AccountOutputDto account) {
        labelLogin.setText(account.login());
        labelEmail.setText(account.email());
        labelLocale.setText(account.locale());
        labelFirstName.setText(account.firstName());
        labelLastName.setText(account.lastName());
        labelAccountState.setText(account.accountState());
        labelAccountRoles.setText(String.join(", ", account.accountRoles()));
    }


    private void setUpAddressLabels(AddressDto address) {
        labelStreet.setText(address.street());
        labelHouseNumber.setText(address.houseNumber().toString());
        labelCity.setText(address.city());
        labelPostalCode.setText(address.postalCode());
        labelCountry.setText(address.country());
    }


    private void setUpAuthLogsLabels(AuthLogOutputDto authLogs) {
        String lastUnsuccessfulAuthIpAddr = authLogs.getLastUnsuccessfulAuthIpAddr().orElse("-");

        labelLastSuccessIp.setText(authLogs.lastSuccessfulAuthIpAddr());
        labelLastUnsuccessIp.setText(lastUnsuccessfulAuthIpAddr);
        labelLastSuccessTime.setText(authLogs.getLastSuccessfulAuthTime().orElse("-"));
        labelLastUnsuccessTime.setText(authLogs.getLastUnsuccessfulAuthTime().orElse("-"));
        labelUnsuccessCounter.setText(authLogs.unsuccessfulAuthCounter().toString());
        labelBlockadeEnd.setText(authLogs.getBlockadeEndTime().orElse("-"));
    }


    private void setUpButtons() {
        buttonEdit.setOnAction(event -> openEditContactDialog());
        buttonChangePassword.setOnAction(event -> openEditPasswordDialog());
        buttonChangeEmail.setOnAction(event -> openEditEmailDialog());
    }


    private void openEditEmailDialog() {
        Window initialWindow = labelLogin.getScene().getWindow();
        Consumer<TextField> fieldAnimation = animationService::shake;

        var dialog = new ChangeEmailDialog(initialWindow, bundle, this::changeEmail, fieldAnimation);
        dialog.addStyleSheet(Resources.CSS.CHANGE_FORM_DIALOG);
        dialog.showAndWait();
    }


    private void openEditContactDialog() {

    }


    private boolean changeEmail(String newEmail) {
        try {
            var response = httpService.sendChangeOwnEmailRequest(newEmail);

            Platform.runLater(() -> {
                Dialog.builder()
                    .title(bundle.getString("success"))
                    .text(bundle.getString("you.have.changed.email"))
                    .type(INFO)
                    .display();
            });

            return true;

        } catch (ApplicationException e) {
            e.printStackTrace();

            return false;
        }
    }


    private void openEditPasswordDialog() {
        Window initialWindow = labelLogin.getScene().getWindow();
        Consumer<TextField> fieldAnimation = animationService::shake;

        var dialog = new ChangePasswordDialog(initialWindow, bundle, this::changePassword, fieldAnimation);
        dialog.addStyleSheet(Resources.CSS.CHANGE_FORM_DIALOG);
        dialog.showAndWait();
    }


    private boolean changePassword(ChangePasswordDto dto) {
        try {
            httpService.sendChangeOwnPasswordRequest(dto);

            Platform.runLater(() -> {
                Dialog.builder()
                    .title(bundle.getString("success"))
                    .text(bundle.getString("you.have.changed.password"))
                    .type(INFO)
                    .display();
            });

            return true;

        } catch (ApplicationException e) {
            e.printStackTrace();

            return false;
        }
    }
}
