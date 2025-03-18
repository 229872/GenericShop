package pl.lodz.p.edu.genericshopdesktopfrontend.component.alert;

import javafx.scene.control.Alert;

public class Dialog extends Alert {

    public Dialog(AlertType alertType) {
        super(alertType);
    }

    public Dialog title(String title) {
        setTitle(title);
        return this;
    }

    public Dialog headerText(String headerText) {
        setHeaderText(headerText);
        return this;
    }

    public Dialog contentText(String contentText) {
        setContentText(contentText);
        return this;
    }
}
