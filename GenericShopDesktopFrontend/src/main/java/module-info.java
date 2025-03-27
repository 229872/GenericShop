module pl.lodz.p.edu.genericshopdesktopfrontend {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.nimbusds.jose.jwt;
    requires de.jensd.fx.glyphs.fontawesome;
    requires org.controlsfx.controls;

    opens pl.lodz.p.edu.genericshopdesktopfrontend.controller to javafx.fxml;
    opens pl.lodz.p.edu.genericshopdesktopfrontend to javafx.fxml;

    exports pl.lodz.p.edu.genericshopdesktopfrontend;
    exports pl.lodz.p.edu.genericshopdesktopfrontend.model to com.fasterxml.jackson.databind;
}