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

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.CONFIRM;

class MainSceneController implements Controller, Initializable {

    private final String SETTINGS_SCENE = "/view/scene/sub/settings/settings_scene";
    private final String ACCOUNT_SCENE = "/view/scene/sub/account/account_scene";

    private final SceneManager sceneManager;
    private final Services services;
    private final ResourceBundle bundle;

    private Runnable initActivePanel = initEmptyPanel();


    MainSceneController(SceneManager sceneManager, Services services, ResourceBundle bundle) {
        this.services = requireNonNull(services);
        this.sceneManager = requireNonNull(sceneManager);
        this.bundle = requireNonNull(bundle);

        requireNonNull(services.fxml());
        requireNonNull(services.http());
        requireNonNull(services.auth());
        requireNonNull(services.animation());
        requireNonNull(services.image());
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


    private void setUpButtons(ResourceBundle bundle) {
        buttonAccount.setOnAction(actionEvent -> showAccountPanel());
        buttonCart.setOnAction(actionEvent -> showCart());
        buttonHome.setOnAction(actionEvent -> showHomePanel());
        buttonOrders.setOnAction(actionEvent -> showOrdersPanel());
        buttonSettings.setOnAction(actionEvent -> showSettingsPanel());
        buttonSignOut.setOnAction(actionEvent -> signOut(bundle));
    }


    private void showAccountPanel() {
        try {
            var controller = new AccountSubSceneController(services, bundle);
            Parent panel = services.fxml().load(ACCOUNT_SCENE, controller, Locale.getDefault());
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
            Parent panel = services.fxml().load(SETTINGS_SCENE, controller, Locale.getDefault());
            setUpCenterPanel(panel);
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
