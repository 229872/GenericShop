package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

class MainSceneController implements Controller, Initializable {

    private final SceneManager sceneManager;

    MainSceneController(SceneManager sceneManager) {
        this.sceneManager = requireNonNull(sceneManager);
    }

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button buttonMenu, buttonAccount, buttonCart, buttonHome, buttonOrders, buttonSignOut;

    @FXML
    private VBox vboxLanguage, vboxActiveRole;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setUpButtons();
    }

    private void setUpButtons() {

        buttonMenu.setOnAction(actionEvent -> showMenuBar());
        buttonAccount.setOnAction(actionEvent -> showAccountPanel());
        buttonCart.setOnAction(actionEvent -> showCart());
        buttonHome.setOnAction(actionEvent -> showHomePanel());
        buttonOrders.setOnAction(actionEvent -> showOrdersPanel());
        buttonSignOut.setOnAction(actionEvent -> signOut());
    }

    private void showMenuBar() {

    }

    private void showAccountPanel() {

    }

    private void showCart() {

    }

    private void showHomePanel() {

    }

    private void showOrdersPanel() {

    }

    private void signOut() {

    }
}
