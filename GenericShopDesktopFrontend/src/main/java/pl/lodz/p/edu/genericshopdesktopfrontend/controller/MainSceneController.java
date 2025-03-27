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
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.Services;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.fxml.FXMLService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.image.ImageService;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.CONFIRM;

class MainSceneController implements Controller, Initializable {

    private final String SETTINGS_SCENE = "/view/scene/settings/settings_scene";

    private final SceneManager sceneManager;
    private final FXMLService fxmlService;
    private final AuthService authService;
    private final HttpService httpService;
    private final AnimationService animationService;
    private final ImageService imageService;
    private final Services services;

    private Runnable initActivePanel = initEmptyPanel();


    MainSceneController(SceneManager sceneManager, Services services) {
        this.services = requireNonNull(services);
        this.sceneManager = requireNonNull(sceneManager);
        this.fxmlService = requireNonNull(services.fxml());
        this.httpService = requireNonNull(services.http());
        this.authService = requireNonNull(services.auth());
        this.animationService = requireNonNull(services.animation());
        this.imageService = requireNonNull(services.image());
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
            Image avatar = imageService.loadImage("avatar.png");
            imageViewAvatar.setImage(avatar);
            labelLogin.setText(authService.getLogin().orElse(""));

        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }


    private void setUpButtons(ResourceBundle bundle) {
        buttonAccount.setOnAction(actionEvent -> showAccountPanel());
        buttonCart.setOnAction(actionEvent -> showCart());
        buttonHome.setOnAction(actionEvent -> showHomePanel());
        buttonOrders.setOnAction(actionEvent -> showOrdersPanel());
        buttonSettings.setOnAction(actionEvent -> showSettingsPanel());
        buttonSignOut.setOnAction(actionEvent -> signOut(bundle));
    }


    private void showAccountPanel() {

    }


    private void showCart() {

    }


    private void showHomePanel() {

    }


    private void showOrdersPanel() {

    }


    private void showSettingsPanel() {
        try {
            var settingsSceneController = new SettingsSceneController(sceneManager, services);

            Parent panel = fxmlService.load(SETTINGS_SCENE, settingsSceneController, Locale.getDefault());
            borderPane.setCenter(panel);
            BorderPane.setMargin(panel, new Insets(100, 50, 100, 50));
            initActivePanel = () -> buttonSettings.fire();

        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }


    private void signOut(ResourceBundle bundle) {
        Dialog.builder()
            .type(CONFIRM)
            .title(bundle.getString("logout.dialog.title"))
            .header(bundle.getString("logout.dialog.header"))
            .text(bundle.getString("logout.dialog.content"))
            .build()
            .showAndWait()
            .filter(buttonType -> buttonType.equals(ButtonType.OK))
            .ifPresent(none -> {
                authService.logout();
                initActivePanel = initEmptyPanel();
                sceneManager.switchToAuthenticationScene();
            });
    }


    private static Runnable initEmptyPanel() {
        return () -> {};
    }
}
