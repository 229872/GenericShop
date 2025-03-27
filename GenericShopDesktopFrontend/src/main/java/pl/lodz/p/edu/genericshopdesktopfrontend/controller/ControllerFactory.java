package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import pl.lodz.p.edu.genericshopdesktopfrontend.scene.SceneLoader;
import pl.lodz.p.edu.genericshopdesktopfrontend.scene.SceneManager;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthenticationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;

import java.util.Optional;

public class ControllerFactory {

    private ControllerFactory() {}


    private static Controller mainScene;
    private static Controller authenticationScene;


    public static Controller getMainSceneController(SceneManager sceneManager, SceneLoader sceneLoader,
                                                    AuthenticationService authenticationService) {

        return Optional
            .ofNullable(mainScene)
            .orElseGet(() -> {
                mainScene = new MainSceneController(sceneManager, sceneLoader, authenticationService);
                return mainScene;
            });
    }


    public static Controller getAuthSceneController(AnimationService animationService, SceneManager sceneManager,
                                                    HttpService httpService, AuthenticationService authenticationService) {

        return Optional
            .ofNullable(authenticationScene)
            .orElseGet(() -> {
                return authenticationScene =
                new AuthenticationSceneController(animationService, sceneManager, httpService, authenticationService);
            });
    }
}
