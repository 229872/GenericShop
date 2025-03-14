package pl.lodz.p.edu.genericshopdesktopfrontend.component.alert;

public class ErrorDialog extends SimpleDialog {

    public ErrorDialog(String headerText, String contentText) {
        super(AlertType.ERROR, "Error occurred.", headerText, contentText);
    }
}
