module pl.lodz.p.edu.genericshopdesktopfrontend {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.nimbusds.jose.jwt;

    opens pl.lodz.p.edu.genericshopdesktopfrontend.controller to javafx.fxml;

    exports pl.lodz.p.edu.genericshopdesktopfrontend;
}