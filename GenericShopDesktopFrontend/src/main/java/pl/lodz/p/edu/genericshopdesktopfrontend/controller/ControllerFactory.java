package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import pl.lodz.p.edu.genericshopdesktopfrontend.scene.SceneLoader;
import pl.lodz.p.edu.genericshopdesktopfrontend.scene.SceneManager;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;

import java.util.Optional;

public class ControllerFactory {

    private ControllerFactory() {}


    private static Controller mainScene;
    private static Controller authenticationScene;


    public static Controller getMainSceneController(SceneManager sceneManager,
                                                    SceneLoader sceneLoader,
                                                    HttpService httpService,
                                                    AnimationService animationService) {
        return Optional
            .ofNullable(mainScene)
            .orElseGet(() -> {
                mainScene = new MainSceneController(sceneManager, sceneLoader, httpService, animationService);
                return mainScene;
            });
    }


    public static Controller getAuthSceneController(AnimationService animationService,
                                                    SceneManager sceneManager,
                                                    HttpService httpService) {
        return Optional
            .ofNullable(authenticationScene)
            .orElseGet(() -> {
                return authenticationScene =
                new AuthSceneController(animationService, sceneManager, httpService);
            });
    }
}
