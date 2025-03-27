package pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog;

import javafx.scene.control.Alert;

class JavaFXDialog extends Alert {

    JavaFXDialog(DialogBuilder builder) {
        super(mapTo(builder.getType()));

        setTitle(builder.getTitle());
        setHeaderText(builder.getHeader());
        setContentText(builder.getText());
    }


    private static AlertType mapTo(Dialog.DialogType dialogType) {
        return switch (dialogType) {
            case INFO -> AlertType.INFORMATION;
            case WARN -> AlertType.WARNING;
            case ERROR -> AlertType.ERROR;
            case CONFIRM -> AlertType.CONFIRMATION;
            case NONE -> AlertType.NONE;
        };
    }
}
