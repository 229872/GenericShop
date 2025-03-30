package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import pl.lodz.p.edu.genericshopdesktopfrontend.SceneManager;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.Services;

import java.util.Optional;
import java.util.ResourceBundle;

public class ControllerFactory {

    private ControllerFactory() {
        throw new UnsupportedOperationException("Can't instantiate static class.");
    }


    private static Controller dashboardScene;
    private static Controller authenticationScene;


    public static Controller getDashboardScene(SceneManager sceneManager, Services services, ResourceBundle bundle) {
        return Optional.ofNullable(dashboardScene)
            .orElseGet(() -> {
                return dashboardScene = new DashboardSceneController(sceneManager, services, bundle);
            });
    }


    public static Controller getAuthSceneController(SceneManager sceneManager, Services services) {
        return Optional.ofNullable(authenticationScene)
            .orElseGet(() -> {
                return authenticationScene = new AuthSceneController(sceneManager, services);
            });
    }
}
