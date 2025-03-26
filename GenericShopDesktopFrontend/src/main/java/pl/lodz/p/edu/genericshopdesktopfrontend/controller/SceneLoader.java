package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

class SceneLoader {

     Scene loadScene(String scenePathWithoutExtension, Controller controller,
                     Locale applicationLanguage) throws ApplicationException {
        try {
            String fxmlPath = "%s.fxml".formatted(scenePathWithoutExtension);
            String cssPath = "%s.css".formatted(scenePathWithoutExtension);

            String i18nBundlePath = scenePathWithoutExtension
                .substring(1)
                .replace("/", ".")
                .concat("_i18n");

            URL fxmlURL = requireNonNull(getClass().getResource(fxmlPath));
            ResourceBundle i18nResource = ResourceBundle.getBundle(i18nBundlePath, applicationLanguage);

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlURL);
            fxmlLoader.setController(controller);
            fxmlLoader.setResources(i18nResource);
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent);

            Optional.ofNullable(getClass().getResource(cssPath))
                .ifPresent(cssURL -> scene.getStylesheets().add(cssURL.toExternalForm()));

            return scene;

        } catch (IOException | NullPointerException e) {
            throw new ApplicationException("Can't switch to Authentication scene", e);
        }
    }
}
