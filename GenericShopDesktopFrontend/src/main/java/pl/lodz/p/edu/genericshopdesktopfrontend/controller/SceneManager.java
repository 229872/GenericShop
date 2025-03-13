package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;

import java.io.IOException;
import java.net.URL;

import static java.util.Objects.requireNonNull;

public class SceneManager {

    private final String AUTHENTICATION_SCENE = "/view/scene/authentication/authentication_scene.fxml";

    private final Stage primaryStage;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = requireNonNull(primaryStage);
    }

    public void switchToAuthenticationScene() throws ApplicationException {
        loadScene(AUTHENTICATION_SCENE, new AuthenticationController());
    }

    private void loadScene(String fxmlPath, Controller controller) throws ApplicationException {
        try {
            URL fxmlURL = getClass().getResource(fxmlPath);

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
            fxmlLoader.setController(controller);
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);

        } catch (IOException e) {
            throw new ApplicationException("Can't switch to Authentication scene", e);
        }
    }
}
