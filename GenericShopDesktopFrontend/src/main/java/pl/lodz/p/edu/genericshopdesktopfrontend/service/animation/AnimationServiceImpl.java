package pl.lodz.p.edu.genericshopdesktopfrontend.service.animation;

import javafx.animation.TranslateTransition;
import javafx.scene.control.TextInputControl;
import javafx.util.Duration;

class AnimationServiceImpl implements AnimationService {

    @Override
    public void shakeField(TextInputControl control, Duration duration) {
        TranslateTransition tt = new TranslateTransition(duration, control);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.play();
    }


    @Override
    public void shakeField(TextInputControl control) {
        shakeField(control, Duration.millis(100));
    }
}
