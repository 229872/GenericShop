package pl.lodz.p.edu.genericshopdesktopfrontend.service.fxml;

import javafx.scene.Parent;
import pl.lodz.p.edu.genericshopdesktopfrontend.controller.Controller;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;

import java.util.Locale;

public interface FXMLService {

    static FXMLService getInstance() {
        return new FXMLServiceImpl();
    }

    Parent load(String scenePathWithoutExtension, Controller controller, Locale applicationLanguage) throws ApplicationException;
}
