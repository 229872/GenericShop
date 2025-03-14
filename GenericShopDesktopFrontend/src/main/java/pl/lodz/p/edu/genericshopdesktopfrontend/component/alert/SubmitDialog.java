package pl.lodz.p.edu.genericshopdesktopfrontend.component.alert;

public class SubmitDialog extends SimpleDialog {

    public SubmitDialog(String headerText, String contentText) {
        super(AlertType.CONFIRMATION, "Submit operation.", headerText, contentText);
    }
}
