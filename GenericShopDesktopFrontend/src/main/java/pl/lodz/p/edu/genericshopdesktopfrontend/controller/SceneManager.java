package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;

import java.io.IOException;
import java.net.URL;

import static java.util.Objects.requireNonNull;

public class SceneManager {

    private final String AUTHENTICATION_SCENE = "/view/scene/authentication/authentication_scene";

    private final Stage primaryStage;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = requireNonNull(primaryStage);
    }

    public void switchToAuthenticationScene() throws ApplicationException {
        loadScene(AUTHENTICATION_SCENE, new AuthenticationController(AnimationService.getInstance()));
    }

    private void loadScene(String sceneStem, Controller controller) throws ApplicationException {
        try {
            String fxmlPath = "%s.fxml".formatted(sceneStem);
            String cssPath = "%s.css".formatted(sceneStem);
            URL fxmlURL = requireNonNull(getClass().getResource(fxmlPath));
            URL cssURL = requireNonNull(getClass().getResource(cssPath));

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
            fxmlLoader.setController(controller);
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent);
            scene.getStylesheets().add(cssURL.toExternalForm());
            primaryStage.setScene(scene);

        } catch (IOException | NullPointerException e) {
            throw new ApplicationException("Can't switch to Authentication scene", e);
        }
    }
}
