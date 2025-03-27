package pl.lodz.p.edu.genericshopdesktopfrontend.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import pl.lodz.p.edu.genericshopdesktopfrontend.controller.Controller;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

public class SceneLoader {

     public Scene loadScene(String scenePathWithoutExtension, Controller controller,
                            Locale applicationLanguage) throws ApplicationException {
        try {
            URL fxmlURL = loadFxml(scenePathWithoutExtension);
            ResourceBundle i18nResource = loadBundle(scenePathWithoutExtension, applicationLanguage);

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
            fxmlLoader.setController(controller);
            fxmlLoader.setResources(i18nResource);

            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);

            loadCss(scenePathWithoutExtension)
                .ifPresent(cssURL -> parent.getStylesheets().add(cssURL.toExternalForm()));

            return scene;

        } catch (IOException | NullPointerException e) {
            throw new ApplicationException("Can't switch to Authentication scene", e);
        }
    }


    private ResourceBundle loadBundle(String scenePathWithoutExtension, Locale applicationLanguage) {
        String i18nBundlePath = scenePathWithoutExtension
            .substring(1)
            .replace("/", ".")
            .concat("_i18n");

        return ResourceBundle.getBundle(i18nBundlePath, applicationLanguage);
    }


    private URL loadFxml(String scenePathWithoutExtension) {
        String fxmlPath = "%s.fxml".formatted(scenePathWithoutExtension);
        return requireNonNull(getClass().getResource(fxmlPath), "Fxml required");
    }


    private Optional<URL> loadCss(String scenePathWithoutExtension) {
        String cssPath = "%s.css".formatted(scenePathWithoutExtension);
        return Optional.ofNullable(getClass().getResource(cssPath));
    }
}
