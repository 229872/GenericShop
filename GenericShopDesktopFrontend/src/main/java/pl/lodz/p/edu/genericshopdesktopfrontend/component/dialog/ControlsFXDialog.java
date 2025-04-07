package pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog;

import javafx.geometry.Pos;
import org.controlsfx.control.Notifications;

class ControlsFXDialog  {

    ControlsFXDialog(DialogBuilder builder) {
        Notifications noti = Notifications.create()
            .title(builder.getTitle())
            .text(builder.getText())
            .position(Pos.BOTTOM_RIGHT);

        switch (builder.getType()) {
            case INFO -> noti.showInformation();
            case WARN -> noti.showWarning();
            case ERROR -> noti.showError();
            case CONFIRM -> noti.showConfirm();
            case NONE -> noti.show();
        }
    }
}
