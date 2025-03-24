package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.controlsfx.control.Notifications;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.alert.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthenticationService;

import java.net.URL;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

class MainSceneController implements Controller, Initializable {

    private final SceneManager sceneManager;

    private final AuthenticationService authenticationService;

    MainSceneController(SceneManager sceneManager, AuthenticationService authenticationService) {
        this.sceneManager = requireNonNull(sceneManager);
        this.authenticationService = requireNonNull(authenticationService);
    }

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button buttonMenu, buttonAccount, buttonCart, buttonHome, buttonOrders;

    @FXML
    private Button buttonLanguage, buttonActiveRole, buttonSignOut;

    @FXML
    private VBox vboxLanguage, vboxActiveRole;

    @FXML
    private FontAwesomeIconView iconLanguageArrow, iconActiveRoleArrow;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setUpButtons(resourceBundle);
        setUpGroups();
    }

    private void setUpButtons(ResourceBundle bundle) {

        buttonMenu.setOnAction(actionEvent -> showMenuBar());
        buttonAccount.setOnAction(actionEvent -> showAccountPanel());
        buttonCart.setOnAction(actionEvent -> showCart());
        buttonHome.setOnAction(actionEvent -> showHomePanel());
        buttonOrders.setOnAction(actionEvent -> showOrdersPanel());

        buttonLanguage.setOnAction(actionEvent -> toggleLanguage());
        buttonActiveRole.setOnAction(actionEvent -> toggleActiveRole());
        buttonSignOut.setOnAction(actionEvent -> signOut(bundle));
    }

    private void setUpGroups() {

        Font buttonsFont = Font.font(15.0);

    }

    private void toggleLanguage() {
        boolean isVisible = vboxLanguage.isVisible();
        vboxLanguage.setVisible(!isVisible);
        vboxLanguage.setManaged(!isVisible);
        iconLanguageArrow.setGlyphName(isVisible ? "CARET_RIGHT" : "CARET_DOWN");
    }

    private void toggleActiveRole() {
        boolean isVisible = vboxActiveRole.isVisible();
        vboxActiveRole.setVisible(!isVisible);
        vboxActiveRole.setManaged(!isVisible);
        iconActiveRoleArrow.setGlyphName(isVisible ? "CARET_RIGHT" : "CARET_DOWN");
    }

    private void showMenuBar() {

    }

    private void showAccountPanel() {

    }

    private void showCart() {

    }

    private void showHomePanel() {

    }

    private void showOrdersPanel() {

    }

    private void signOut(ResourceBundle bundle) {

        new Dialog(Alert.AlertType.CONFIRMATION)
            .title(bundle.getString("logout.dialog.title"))
            .headerText(bundle.getString("logout.dialog.header"))
            .contentText(bundle.getString("logout.dialog.content"))
            .showAndWait()
            .filter(buttonType -> buttonType.equals(ButtonType.OK))
            .ifPresent(none -> {
                try {
                    authenticationService.logout();
                    sceneManager.switchToAuthenticationScene();

                } catch (ApplicationException e) {

                    e.printStackTrace();
                    Notifications.create()
                        .showError();
                }
            });
    }
}
