package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import pl.lodz.p.edu.genericshopdesktopfrontend.SceneManager;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.Services;

import java.util.Optional;

public class ControllerFactory {

    private ControllerFactory() {}


    private static Controller mainScene;
    private static Controller authenticationScene;


    public static Controller getMainSceneController(SceneManager sceneManager, Services services) {
        return Optional.ofNullable(mainScene)
            .orElseGet(() -> {
                return mainScene = new MainSceneController(sceneManager, services);
            });
    }


    public static Controller getAuthSceneController(SceneManager sceneManager, Services services) {
        return Optional.ofNullable(authenticationScene)
            .orElseGet(() -> {
                return authenticationScene = new AuthSceneController(sceneManager, services);
            });
    }
}
