package pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog;

import javafx.scene.control.Alert;

class DialogBuilder implements Dialog {

    private String title = "";
    private String header = "";
    private String text = "";
    private DialogType type = DialogType.NONE;


    @Override
    public Dialog type(DialogType type) {
        this.type = type;
        return this;
    }

    @Override
    public Dialog title(String title) {
        this.title = title;
        return this;
    }

    
    @Override
    public Dialog header(String text) {
        this.header = text;
        return this;
    }

    
    @Override
    public Dialog text(String text) {
        this.text = text;
        return this;
    }


    @Override
    public Alert build() {
        return new JavaFXDialog(this);
    }


    @Override
    public void display() {
        new ControlsFXDialog(this);
    }


    String getTitle() {
        return title;
    }

    
    String getHeader() {
        return header;
    }

    
    String getText() {
        return text;
    }


    DialogType getType() {
        return type;
    }
}
