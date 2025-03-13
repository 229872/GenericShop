package pl.lodz.p.edu.genericshopdesktopfrontend;

import javafx.application.Application;
import javafx.stage.Stage;

public class DesktopApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Hello");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
