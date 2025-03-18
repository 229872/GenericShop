package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class TitleBarController implements Initializable {

    @FXML
    private Button buttonCloseApp, buttonMinimize;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        buttonMinimize.setOnAction(actionEvent -> fireMinimizeEvent());
        buttonCloseApp.setOnAction(actionEvent -> fireCloseEvent());
    }

    private void fireMinimizeEvent() {
        window()
            .ifPresent(window -> {
                Event.fireEvent(buttonMinimize, new WindowEvent(window, WindowEvent.WINDOW_HIDING));
            });
    }

    private void fireCloseEvent() {
        window()
            .ifPresent(window -> {
                Event.fireEvent(buttonCloseApp, new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
            });
    }

    private Optional<Window> window() {
        return Optional
            .ofNullable(buttonMinimize.getScene())
            .map(Scene::getWindow);
    }
}
