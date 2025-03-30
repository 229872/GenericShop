package pl.lodz.p.edu.genericshopdesktopfrontend.service.fxml;

import javafx.scene.Parent;
import pl.lodz.p.edu.genericshopdesktopfrontend.controller.Controller;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;

import java.util.ResourceBundle;

public interface FXMLService {

    static FXMLService getInstance() {
        return new FXMLServiceImpl();
    }

    Parent load(String scenePathWithoutExtension, Controller controller,  ResourceBundle i18nResource) throws ApplicationException;
}
