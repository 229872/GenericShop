package pl.lodz.p.edu.genericshopdesktopfrontend;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.alert.ErrorDialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.controller.SceneManager;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.image.ImageService;

import java.util.Locale;

public class DesktopApplication extends Application {

    private SceneManager sceneManager;
    private final ImageService imageService;

    public DesktopApplication() {
        this.imageService = ImageService.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            sceneManager = new SceneManager(primaryStage, Locale.getDefault());
            sceneManager.switchToAuthenticationScene();

            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setTitle("Shop");

            Image appIcon = imageService.loadImage("app_icon.jpg");
            primaryStage.getIcons().add(appIcon);
            primaryStage.show();

        } catch (Exception e) {

            e.printStackTrace();
            Alert alert = new ErrorDialog("Critical error occurred.", "App can't start.");
            alert.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


}
