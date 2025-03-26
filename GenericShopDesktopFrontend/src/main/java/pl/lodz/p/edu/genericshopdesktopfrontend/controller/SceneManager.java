package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.alert.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthenticationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;

import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

public class SceneManager {

    private final String AUTHENTICATION_SCENE = "/view/scene/authentication/authentication_scene";
    private final String MAIN_SCENE = "/view/scene/main/main_scene";


    private final Stage primaryStage;
    private final WindowManager windowManager;
    private final String rootBundleName;
    private final SceneLoader sceneLoader;
    private ResourceBundle rootLanguageBundle;


    public SceneManager(Stage primaryStage, String rootBundleName) {
        this.primaryStage = requireNonNull(primaryStage);

        this.sceneLoader = new SceneLoader();
        this.rootBundleName = rootBundleName;

        this.rootLanguageBundle = ResourceBundle.getBundle(rootBundleName, Locale.getDefault());
        this.windowManager = new WindowManager(primaryStage, rootLanguageBundle);

        primaryStage.setOnHiding(windowEvent -> windowManager.minimise());
        primaryStage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            windowManager.closeApp();
        });
    }


    public void switchToAuthenticationScene() {
        AuthenticationService authService = AuthenticationService.getInstance();
        HttpService httpService = HttpService.getInstance();

        Controller controller = new AuthenticationSceneController(
            AnimationService.getInstance(), this, httpService, authService);

        loadScene(AUTHENTICATION_SCENE, controller);
    }


    public void switchToMainScene() {
        var authService = AuthenticationService.getInstance();
        var mainSceneController = new MainSceneController(this, sceneLoader, authService);

        loadScene(MAIN_SCENE, mainSceneController);
    }


    private void loadScene(String scenePathWithoutExtension, Controller controller) {
        try {
            Scene scene = sceneLoader.loadScene(scenePathWithoutExtension, controller, Locale.getDefault());
            windowManager.setUpWindowDragging(scene);
            primaryStage.setScene(scene);

        } catch (ApplicationException e) {
            e.printStackTrace();
            showErrorNotification();
        }
    }


    private void showErrorNotification() {
        Notifications.create()
            .title(rootLanguageBundle.getString("error.title"))
            .text(rootLanguageBundle.getString("error.switchscene.content"))
            .showError();
    }


    public void setApplicationLanguage(Locale newApplicationLanguage) {
        Locale.setDefault(newApplicationLanguage);
        this.rootLanguageBundle = ResourceBundle.getBundle(rootBundleName, newApplicationLanguage);
        windowManager.setBundle(rootLanguageBundle);
    }



    private static class WindowManager {

        private double xOffset, yOffset;
        private final Stage primaryStage;
        private ResourceBundle bundle;

        private WindowManager(Stage primaryStage, ResourceBundle bundle) {
            this.primaryStage = primaryStage;
            this.bundle = bundle;
        }

        private void setBundle(ResourceBundle bundle) {
            this.bundle = bundle;
        }

        private void setUpWindowDragging(Scene scene) {
            scene.setOnMousePressed(mouseEvent -> {
                xOffset = mouseEvent.getX();
                yOffset = mouseEvent.getY();
            });

            scene.setOnMouseDragged(mouseEvent -> {
                primaryStage.setX(mouseEvent.getScreenX() - xOffset);
                primaryStage.setY(mouseEvent.getScreenY() - yOffset);
            });
        }

        private void closeApp() {
            Alert alert = new Dialog(AlertType.CONFIRMATION)
                .title(bundle.getString("exit.title"))
                .headerText(bundle.getString("exit.header"))
                .contentText(bundle.getString("exit.content"));

            alert.showAndWait()
                .filter(buttonType -> buttonType.equals(ButtonType.OK))
                .ifPresent(none -> Platform.exit());
        }

        private void minimise() {
            if (primaryStage.isShowing()) {
                primaryStage.setIconified(true);
            }
        }
    }
}
