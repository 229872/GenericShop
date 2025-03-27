package pl.lodz.p.edu.genericshopdesktopfrontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import pl.lodz.p.edu.genericshopdesktopfrontend.SceneManager;
import pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog;
import pl.lodz.p.edu.genericshopdesktopfrontend.exception.ApplicationException;
import pl.lodz.p.edu.genericshopdesktopfrontend.model.Role;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.Services;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.animation.AnimationService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.auth.AuthService;
import pl.lodz.p.edu.genericshopdesktopfrontend.service.http.HttpService;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static pl.lodz.p.edu.genericshopdesktopfrontend.component.dialog.Dialog.DialogType.ERROR;
import static pl.lodz.p.edu.genericshopdesktopfrontend.util.Utils.setUpDividers;

class SettingsSceneController implements Controller, Initializable {

    private final AuthService authService;
    private final SceneManager sceneManager;
    private final HttpService httpService;
    private final AnimationService animationService;


    SettingsSceneController(SceneManager sceneManager, Services services) {
        requireNonNull(services);
        this.sceneManager = requireNonNull(sceneManager);
        this.httpService = requireNonNull(services.http());
        this.authService = requireNonNull(services.auth());
        this.animationService = requireNonNull(services.animation());
    }


    @FXML
    private VBox vboxRoles, vboxLanguages;

    @FXML
    private SplitPane splitPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpRoleButtons(resourceBundle);
        setUpLanguageButtons(resourceBundle);
        setUpDividers(splitPane);
    }


    private void setUpRoleButtons(ResourceBundle bundle) {
        Font buttonsFont = Font.font(20);
        Color textColor = Color.BLACK;
        ToggleGroup roleGroup = new ToggleGroup();
        Role activeRole = authService.getActiveRole();

        List<RadioButton> radioButtons = authService.getAccountRoles().stream()
            .map(role -> roleToButton(role, bundle, textColor, buttonsFont, activeRole))
            .toList();

        roleGroup.selectedToggleProperty()
            .addListener((observableValue, oldToggle, newToggle) -> {
                if (newToggle.getUserData() instanceof Role newRole) {
                    authService.setActiveRole(newRole);
                }
            });

        roleGroup.getToggles().addAll(radioButtons);
        vboxRoles.getChildren().addAll(radioButtons);
    }

    private void setUpLanguageButtons(ResourceBundle resourceBundle) {
        Consumer<Node> animation = node -> animationService.fade(node, Duration.millis(75), 0.8, 1);
        Font buttonsFont = Font.font(20);
        Color textColor = Color.BLACK;
        ToggleGroup languageGroup = new ToggleGroup();

        List<RadioButton> radioButtons = Stream.of("en", "pl")
            .map(language -> languageToButton(language, textColor, buttonsFont))
            .toList();

        languageGroup.selectedToggleProperty()
            .addListener((observableValue, oldToggle, newToggle) -> {
                if (oldToggle != null && newToggle.getUserData() instanceof Locale newLanguage) {
                    changeLanguage(resourceBundle, newLanguage, animation);
                }
            });

        languageGroup.getToggles().addAll(radioButtons);
        vboxLanguages.getChildren().addAll(radioButtons);
    }


    private void changeLanguage(ResourceBundle resourceBundle, Locale newLanguage, Consumer<Node> animation) {
        Locale.setDefault(newLanguage);
        changeLanguageOnServer(newLanguage.getLanguage(), resourceBundle);
        sceneManager.setApplicationLanguage(newLanguage);
        sceneManager.switchToMainScene(animation);
    }


    private void changeLanguageOnServer(String language, ResourceBundle bundle) {
        try {
            httpService.sendChangeAccountLanguageRequest(language);

        } catch (ApplicationException e) {
            e.printStackTrace();

            Dialog.builder()
                .type(ERROR)
                .title(bundle.getString("error.title"))
                .text(bundle.getString("error.text"))
                .display();
        }
    }


    private RadioButton setUpButton(String label, Color textColor, Font buttonsFont, Object data) {
        var button = new RadioButton(label);
        button.setTextFill(textColor);
        button.setFont(buttonsFont);
        button.setUserData(data);
        return button;
    }


    private RadioButton roleToButton(Role role, ResourceBundle bundle, Color textColor, Font buttonsFont, Role activeRole) {
        String buttonName = bundle.getString(role.name().toLowerCase());
        var button = setUpButton(buttonName, textColor, buttonsFont, role);

        if (role.equals(activeRole)) {
            button.setSelected(true);
        }

        return button;
    };


    private RadioButton languageToButton(String language, Color textColor, Font buttonsFont)  {
        Locale locale = Locale.forLanguageTag(language);
        RadioButton button = setUpButton(language, textColor, buttonsFont, locale);

        if (Locale.getDefault().getLanguage().equals(language)) {
            button.setSelected(true);
        }

        return button;
    }
}
