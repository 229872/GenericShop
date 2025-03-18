package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.alert.SubmitDialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

public class SceneManager {

    private final String AUTHENTICATION_SCENE = "/view/scene/authentication/authentication_scene";

    private final Stage primaryStage;
    private final WindowManager windowManager;

    private Locale applicationLanguage;

    public SceneManager(Stage primaryStage, Locale applicationLanguage) {

        this.primaryStage = requireNonNull(primaryStage);
        this.applicationLanguage = requireNonNull(applicationLanguage);
        this.windowManager = new WindowManager(primaryStage);

        primaryStage.setOnHiding(windowEvent -> windowManager.minimise());

        primaryStage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            windowManager.closeApp();
        });
    }

    public void switchToAuthenticationScene() throws ApplicationException {
        Controller controller = new AuthenticationController(AnimationService.getInstance(), this);
        loadScene(AUTHENTICATION_SCENE, controller);
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
            URL cssURL = requireNonNull(getClass().getResource(cssPath));
            ResourceBundle i18nResource = ResourceBundle.getBundle(i18nBundlePath, applicationLanguage);

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
            fxmlLoader.setController(controller);
            fxmlLoader.setResources(i18nResource);
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent);
            scene.getStylesheets().add(cssURL.toExternalForm());

            windowManager.setUpWindowDragging(scene);
            primaryStage.setScene(scene);

        } catch (IOException | NullPointerException e) {
            throw new ApplicationException("Can't switch to Authentication scene", e);
        }
    }

    public void setApplicationLanguage(Locale newApplicationLanguage) {
        this.applicationLanguage = newApplicationLanguage;
    }



    private static class WindowManager {

        private double xOffset, yOffset;
        private final Stage primaryStage;

        private WindowManager(Stage primaryStage) {
            this.primaryStage = primaryStage;
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
            Alert alert = new SubmitDialog("You are about to close the app.", "Are you sure you want close app?");

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
