package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.alert.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthenticationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

public class SceneManager {

    private final String AUTHENTICATION_SCENE = "/view/scene/authentication/authentication_scene";
    private final String MAIN_SCENE = "/view/scene/main/main_scene";

    private final Stage primaryStage;
    private final WindowManager windowManager;
    private final String rootBundleName;

    private Locale applicationLanguage;
    private ResourceBundle rootLanguageBundle;

    public SceneManager(Stage primaryStage, Locale applicationLanguage, String rootBundleName) {

        this.primaryStage = requireNonNull(primaryStage);
        this.applicationLanguage = requireNonNull(applicationLanguage);
        this.rootBundleName = rootBundleName;

        this.rootLanguageBundle = ResourceBundle.getBundle(rootBundleName, applicationLanguage);
        this.windowManager = new WindowManager(primaryStage, rootLanguageBundle);

        primaryStage.setOnHiding(windowEvent -> windowManager.minimise());

        primaryStage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            windowManager.closeApp();
        });
    }

    public void switchToAuthenticationScene() throws ApplicationException {
        AuthenticationService authService = AuthenticationService.getInstance();
        HttpService httpService = HttpService.getInstance();
        Controller controller = new AuthenticationSceneController(
            AnimationService.getInstance(), this, httpService, authService
        );

        loadScene(AUTHENTICATION_SCENE, controller);
    }

    public void switchToMainScene() throws ApplicationException {

        loadScene(MAIN_SCENE, new MainSceneController(this));
    }

    private void loadScene(String scenePathWithoutExtension, Controller controller) throws ApplicationException {
        try {
            String fxmlPath = "%s.fxml".formatted(scenePathWithoutExtension);
            String cssPath = "%s.css".formatted(scenePathWithoutExtension);

            String i18nBundlePath = scenePathWithoutExtension
                .substring(1)
                .replace("/", ".")
                .concat("_i18n");

            URL fxmlURL = requireNonNull(getClass().getResource(fxmlPath));
            ResourceBundle i18nResource = ResourceBundle.getBundle(i18nBundlePath, applicationLanguage);

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
            fxmlLoader.setController(controller);
            fxmlLoader.setResources(i18nResource);
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent);

            Optional.ofNullable(getClass().getResource(cssPath))
                .ifPresent(cssURL -> scene.getStylesheets().add(cssURL.toExternalForm()));

            windowManager.setUpWindowDragging(scene);
            primaryStage.setScene(scene);

        } catch (IOException | NullPointerException e) {
            throw new ApplicationException("Can't switch to Authentication scene", e);
        }
    }

    public void setApplicationLanguage(Locale newApplicationLanguage) {
        this.applicationLanguage = newApplicationLanguage;
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
