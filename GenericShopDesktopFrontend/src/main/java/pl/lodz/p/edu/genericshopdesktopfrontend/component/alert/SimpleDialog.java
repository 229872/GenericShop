package pl.lodz.p.edu.genericshopdesktopfrontend.component.alert;

import javafx.scene.control.Alert;

public class SimpleDialog extends Alert {

    public SimpleDialog(AlertType type, String title, String headerText, String contentText) {
        super(type);
        setTitle(title);
        setHeaderText(headerText);
        setContentText(contentText);
    }
}
