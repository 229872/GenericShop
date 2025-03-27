package pl.lodz.p.edu.genericshopdesktopfrontend.scene;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.util.Duration;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.controller.Controller;
import pl.lodz.p.edu.genericshopdesktopfrontend.controller.ControllerFactory;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.CONFIRM;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.ERROR;

public class SceneManager {

    private final String AUTHENTICATION_SCENE = "/view/scene/authentication/authentication_scene";
    private final String MAIN_SCENE = "/view/scene/main/main_scene";


    private final Stage primaryStage;
    private final WindowManager windowManager;
    private final SceneLoader sceneLoader;

    private final HttpService httpService;
    private final AnimationService animationService;

    private final String rootBundleName;
    private ResourceBundle rootLanguageBundle;


    public SceneManager(Stage primaryStage, HttpService httpService, AnimationService animationService, String rootBundleName) {
        this.primaryStage = requireNonNull(primaryStage);
        this.httpService = requireNonNull(httpService);
        this.animationService = requireNonNull(animationService);

        this.sceneLoader = new SceneLoader();
        this.rootBundleName = rootBundleName;

        this.rootLanguageBundle = ResourceBundle.getBundle(rootBundleName, Locale.getDefault());
        this.windowManager = new WindowManager(primaryStage, rootLanguageBundle);

        primaryStage.setOnHiding(windowEvent -> windowManager.minimise());
        primaryStage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            windowManager.closeApp();
        });
    }


    public void switchToAuthenticationScene() {
        Controller controller = ControllerFactory.getAuthSceneController(
            AnimationService.getInstance(), this, httpService
        );

        Consumer<Node> animation = node -> animationService.fade(node, Duration.seconds(1.5));

        loadScene(AUTHENTICATION_SCENE, controller, animation);
    }


    public void switchToMainScene(Consumer<Node> animation) {
        Controller mainSceneController = ControllerFactory.getMainSceneController(
            this, sceneLoader, httpService, animationService
        );

        loadScene(MAIN_SCENE, mainSceneController, animation);
    }


    public void switchToMainScene() {
        Consumer<Node> animation = node ->
            animationService.fade(node, Duration.millis(500), 0.3, 1);

        switchToMainScene(animation);
    }


    private void loadScene(String scenePathWithoutExtension, Controller controller, Consumer<Node> animation) {
        try {
            Scene scene = sceneLoader.loadScene(scenePathWithoutExtension, controller, Locale.getDefault());
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
