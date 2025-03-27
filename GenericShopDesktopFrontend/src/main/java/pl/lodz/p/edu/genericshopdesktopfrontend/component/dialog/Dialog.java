package pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog;

import javafx.scene.control.Alert;

public interface Dialog {

    static Dialog builder() {
        return new DialogBuilder();
    }

    Dialog type(DialogType type);

    Dialog title(String title);

    Dialog header(String text);

    Dialog text(String text);

    Alert build();

    void display();


    enum DialogType {
        INFO, WARN, ERROR, CONFIRM, NONE
    }

}
