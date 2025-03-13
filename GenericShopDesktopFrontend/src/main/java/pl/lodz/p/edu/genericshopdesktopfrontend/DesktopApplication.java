package pl.lodz.p.edu.genericshopdesktopfrontend;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.lodz.p.edu.genericshopdesktopfrontend.controller.SceneManager;

public class DesktopApplication extends Application {

    private SceneManager sceneManager;

    @Override
    public void start(Stage primaryStage) throws Exception {

        sceneManager = new SceneManager(primaryStage);
        sceneManager.switchToAuthenticationScene();

        primaryStage.setTitle("Generic Shop");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
