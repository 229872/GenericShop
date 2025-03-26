package pl.lodz.p.edu.genericshopdesktopfrontend;

import javafx.application.Application;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.alert.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.controller.SceneManager;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.image.ImageService;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class DesktopApplication extends Application {

    private SceneManager sceneManager;
    private final ImageService imageService;


    public DesktopApplication() {
        this.imageService = ImageService.getInstance();
    }


    @Override
    public void start(Stage primaryStage) {
        String rootBundleName = "bundles.i18n";
        ResourceBundle rootLanguageBundle = null;

        try {
            rootLanguageBundle = ResourceBundle.getBundle(rootBundleName, Locale.getDefault());

            sceneManager = new SceneManager(primaryStage,"bundles.i18n");
            sceneManager.switchToAuthenticationScene();

            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setTitle(rootLanguageBundle.getString("title"));

            Image appIcon = imageService.loadImage("app_icon.jpg");
            primaryStage.getIcons().add(appIcon);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            var bundle = Optional.ofNullable(rootLanguageBundle);

            new Dialog(AlertType.ERROR)
                .title(bundle
                    .map(b -> b.getString("error.title"))
                    .orElse("Error"))
                .contentText(bundle
                    .map(b -> b.getString("error.content"))
                    .orElse("Couldn't start application"))
                .show();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
