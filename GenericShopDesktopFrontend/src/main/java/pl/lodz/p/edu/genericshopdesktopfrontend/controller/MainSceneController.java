package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.scene.SceneLoader;
import pl.lodz.p.edu.genericshopdesktopfrontend.scene.SceneManager;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.CONFIRM;

class MainSceneController implements Controller, Initializable {

    private final String SETTINGS_SCENE = "/view/scene/settings/settings_scene";


    private final SceneManager sceneManager;
    private final SceneLoader sceneLoader;
    private final AuthService authService;
    private final HttpService httpService;
    private final AnimationService animationService;

    private Runnable initActivePanel = initEmptyPanel();


    MainSceneController(SceneManager sceneManager, SceneLoader sceneLoader, HttpService httpService, AnimationService animationService) {
        this.sceneManager = requireNonNull(sceneManager);
        this.sceneLoader = requireNonNull(sceneLoader);
        this.httpService = requireNonNull(httpService);
        this.authService = requireNonNull(httpService.getAuthService());
        this.animationService = requireNonNull(animationService);
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
        Image image = new Image(getClass().getResource("/images/avatar.png").toExternalForm());
        imageViewAvatar.setImage(image);

        labelLogin.setText(authService.getLogin().orElse(""));

        setUpButtons(resourceBundle);
        initActivePanel.run();
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
            SettingsSceneController settingsSceneController =
                new SettingsSceneController(sceneManager, httpService, animationService);

            Scene scene = sceneLoader.loadScene(SETTINGS_SCENE, settingsSceneController, Locale.getDefault());
            Parent panel = scene.getRoot();
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
