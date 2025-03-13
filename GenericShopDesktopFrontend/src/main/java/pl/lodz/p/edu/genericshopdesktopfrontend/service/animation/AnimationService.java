package pl.lodz.p.edu.genericshopdesktopfrontend.service.animation;

import javafx.scene.control.TextInputControl;
import javafx.util.Duration;

public interface AnimationService {

    static AnimationService getInstance() {
        return new AnimationServiceImpl();
    }

    void shakeField(TextInputControl control, Duration duration);

    void shakeField(TextInputControl control);
}
