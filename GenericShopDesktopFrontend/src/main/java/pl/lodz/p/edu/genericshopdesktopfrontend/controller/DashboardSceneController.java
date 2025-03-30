package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pl.lodz.p.edu.genericshopdesktopfrontend.SceneManager;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.config.Resources;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.Services;

import java.net.URL;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.CONFIRM;

class DashboardSceneController implements Controller, Initializable {

    private final SceneManager sceneManager;
    private final Services services;
    private ResourceBundle i18n;

    private Runnable initActivePanel = initEmptyPanel();


    DashboardSceneController(SceneManager sceneManager, Services services, ResourceBundle i18n) {
        this.services = requireNonNull(services);
        this.sceneManager = requireNonNull(sceneManager);
        this.i18n = requireNonNull(i18n);

        requireNonNull(services.fxml());
        requireNonNull(services.http());
        requireNonNull(services.auth());
        requireNonNull(services.animation());
        requireNonNull(services.image());
    }


    public DashboardSceneController setI18n(ResourceBundle i18n) {
        this.i18n = requireNonNull(i18n);
        return this;
    }

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button buttonAccount, buttonCart, buttonHome, buttonOrders;

    @FXML
    private Button buttonSettings, buttonSignOut;

    @FXML
    private VBox vboxLanguage, vboxActiveRole;

    @FXML
    private ImageView imageViewAvatar;

    @FXML
    private Label labelLogin;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpAvatar();
        setUpButtons(resourceBundle);
        initActivePanel.run();

    }

    private void setUpAvatar() {
        try {
            Image avatar = services.image().loadImage("avatar.png");
            imageViewAvatar.setImage(avatar);
            labelLogin.setText(services.auth().getLogin().orElse(""));

        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }


    private void setUpButtons(ResourceBundle i18n) {
        buttonAccount.setOnAction(actionEvent -> showAccountPanel());
        buttonCart.setOnAction(actionEvent -> showCart());
        buttonHome.setOnAction(actionEvent -> showHomePanel());
        buttonOrders.setOnAction(actionEvent -> showOrdersPanel());
        buttonSettings.setOnAction(actionEvent -> showSettingsPanel());
        buttonSignOut.setOnAction(actionEvent -> signOut(i18n));
    }


    private void showAccountPanel() {
        try {
            var controller = new AccountSubSceneController(services, i18n);
            Parent panel = services.fxml().load(Resources.Scene.SUB_ACCOUNT, controller, i18n);
            setUpCenterPanel(panel);
            initActivePanel = () -> buttonAccount.fire();

        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }


    private void showCart() {

    }


    private void showHomePanel() {

    }


    private void showOrdersPanel() {

    }


    private void showSettingsPanel() {
        try {
            var controller = new SettingsSubSceneController(sceneManager, services);
            Parent panel = services.fxml().load(Resources.Scene.SUB_SETTINGS, controller, i18n);
            setUpCenterPanel(panel);
            initActivePanel = () -> buttonSettings.fire();

        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }


    private void signOut(ResourceBundle bundle) {
        Dialog.builder()
            .type(CONFIRM)
            .title(bundle.getString("confirm.operation"))
            .header(bundle.getString("confirm.operation"))
            .text(bundle.getString("are.you.sure.you.want.to.log.out.from.this.account"))
            .build()
            .showAndWait()
            .filter(buttonType -> buttonType.equals(ButtonType.OK))
            .ifPresent(none -> {
                services.auth().logout();
                initActivePanel = initEmptyPanel();
                sceneManager.switchToAuthenticationScene();
            });
    }


    private void setUpCenterPanel(Parent panel) {
        borderPane.setCenter(panel);
        BorderPane.setMargin(panel, new Insets(100, 50, 100, 50));
    }


    private static Runnable initEmptyPanel() {
        return () -> {};
    }
}
