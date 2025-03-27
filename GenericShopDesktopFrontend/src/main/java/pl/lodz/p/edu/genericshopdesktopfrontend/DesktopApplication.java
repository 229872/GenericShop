package pl.lodz.p.edu.genericshopdesktopfrontend;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.Services;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.fxml.FXMLService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.image.ImageService;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.ERROR;

public class DesktopApplication extends Application {

    private final ImageService imageService;
    private final AuthService authService;
    private final HttpService httpService;
    private final AnimationService animationService;
    private final FXMLService fxmlService;


    public DesktopApplication() {
        this.imageService = ImageService.getInstance();
        this.authService = AuthService.getInstance();
        this.httpService = HttpService.getInstance(authService);
        this.animationService = AnimationService.getInstance();
        this.fxmlService = FXMLService.getInstance();
    }


    @Override
    public void start(Stage primaryStage) {
        String rootBundleName = "bundles.i18n";
        ResourceBundle rootLanguageBundle = null;

        try {
            rootLanguageBundle = ResourceBundle.getBundle(rootBundleName, Locale.getDefault());
            Services services = new Services(httpService, authService, animationService, imageService, fxmlService);

            SceneManager sceneManager = new SceneManager(primaryStage, "bundles.i18n", services);
            sceneManager.switchToAuthenticationScene();

            Image appIcon = imageService.loadImage("app_icon.jpg");

            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setTitle(rootLanguageBundle.getString("title"));
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
