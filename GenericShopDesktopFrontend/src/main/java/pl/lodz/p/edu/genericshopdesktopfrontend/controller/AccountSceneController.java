package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.AccountOutputDto;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.AddressOutputDto;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.AuthLogOutputDto;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.Services;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;

import java.net.URL;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.ERROR;

class AccountSceneController implements Controller, Initializable {

    private final HttpService httpService;
    private final AnimationService animationService;


    AccountSceneController(Services services) {
        requireNonNull(services);
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpAccountData(resourceBundle);
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


    private void setUpAddressLabels(AddressOutputDto address) {
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
}
