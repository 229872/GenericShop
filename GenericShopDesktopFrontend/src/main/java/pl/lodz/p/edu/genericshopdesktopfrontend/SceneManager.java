package pl.lodz.p.edu.genericshopdesktopfrontend;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.util.Duration;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.controller.Controller;
import pl.lodz.p.edu.genericshopdesktopfrontend.controller.ControllerFactory;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.Services;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.CONFIRM;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.ERROR;

public class SceneManager {

    private final String AUTHENTICATION_SCENE = "/view/scene/authentication/authentication_scene";
    private final String MAIN_SCENE = "/view/scene/main/main_scene";

    private final String rootBundleName;
    private ResourceBundle rootLanguageBundle;

    private final Stage primaryStage;
    private final WindowManager windowManager;
    private final Services services;


    public SceneManager(Stage primaryStage, String rootBundleName, Services services) {
        this.primaryStage = requireNonNull(primaryStage);
        this.rootBundleName = requireNonNull(rootBundleName);
        this.services = requireNonNull(services);

        requireNonNull(services.http());
        requireNonNull(services.auth());
        requireNonNull(services.animation());
        requireNonNull(services.image());
        requireNonNull(services.fxml());

        this.rootLanguageBundle = ResourceBundle.getBundle(rootBundleName, Locale.getDefault());
        this.windowManager = new WindowManager(primaryStage, rootLanguageBundle);

        primaryStage.setOnHiding(windowEvent -> windowManager.minimise());
        primaryStage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            windowManager.closeApp();
        });
    }


    public void switchToAuthenticationScene() {
        var controller = ControllerFactory.getAuthSceneController(this, services);
        Consumer<Node> animation = node -> services.animation().fade(node, Duration.seconds(1.5));
        loadScene(AUTHENTICATION_SCENE, controller, animation);
    }


    public void switchToMainScene(Consumer<Node> animation) {
        var controller = ControllerFactory.getMainSceneController(this, services, rootLanguageBundle);
        loadScene(MAIN_SCENE, controller, animation);
    }


    private void loadScene(String scenePathWithoutExtension, Controller controller, Consumer<Node> animation) {
        try {
            Parent parent = services.fxml().load(scenePathWithoutExtension, controller, Locale.getDefault());
            Scene scene = new Scene(parent);
            windowManager.setUpWindowDragging(scene);
            primaryStage.setScene(scene);
            animation.accept(scene.getRoot());

        } catch (ApplicationException e) {
            e.printStackTrace();
            showErrorNotification();
        }
    }


    private void showErrorNotification() {
        Dialog.builder()
            .type(ERROR)
            .title(rootLanguageBundle.getString("error.title"))
            .text(rootLanguageBundle.getString("error.switchscene.content"))
            .display();
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
            Dialog.builder()
                .type(CONFIRM)
                .title(bundle.getString("exit.title"))
                .header(bundle.getString("exit.header"))
                .text(bundle.getString("exit.content"))
                .build()
                .showAndWait()
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
