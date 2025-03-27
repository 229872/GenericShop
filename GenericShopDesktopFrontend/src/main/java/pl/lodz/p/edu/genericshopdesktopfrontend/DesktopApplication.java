package pl.lodz.p.edu.genericshopdesktopfrontend;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.scene.SceneManager;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.image.ImageService;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.ERROR;

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
            AuthService authService = AuthService.getInstance();
            HttpService httpService = HttpService.getInstance(authService);

            sceneManager = new SceneManager(primaryStage, httpService,"bundles.i18n");
            sceneManager.switchToAuthenticationScene();

            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setTitle(rootLanguageBundle.getString("title"));

            Image appIcon = imageService.loadImage("app_icon.jpg");
            primaryStage.getIcons().add(appIcon);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            var bundle = Optional.ofNullable(rootLanguageBundle);

            Dialog.builder()
                .type(ERROR)
                .title(bundle
                    .map(b -> b.getString("error.title"))
                    .orElse("Error"))
                .text(bundle
                    .map(b -> b.getString("error.content"))
                    .orElse("Couldn't start application"))
                .display();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
