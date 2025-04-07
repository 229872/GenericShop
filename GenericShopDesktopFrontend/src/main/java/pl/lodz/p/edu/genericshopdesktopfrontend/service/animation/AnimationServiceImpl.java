package pl.lodz.p.edu.genericshopdesktopfrontend.service.animation;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

class AnimationServiceImpl implements AnimationService {

    @Override
    public void shake(Node node, Duration duration, int cycleCount, double from, double by) {
        TranslateTransition tt = new TranslateTransition(duration, node);
        tt.setFromX(from);
        tt.setByX(by);
        tt.setCycleCount(cycleCount);
        tt.setAutoReverse(true);
        tt.play();
    }


    @Override
    public void shake(Node node, Duration duration) {
        shake(node, duration, 4, 0, 10);
    }


    @Override
    public void shake(Node node) {
        shake(node, Duration.millis(100));
    }


    @Override
    public void fade(Node node, Duration duration, double from, double to) {
        FadeTransition fadeIn = new FadeTransition(duration, node);
        fadeIn.setFromValue(from);
        fadeIn.setToValue(to);
        fadeIn.play();
    }


    @Override
    public void fade(Node node, Duration duration) {
        fade(node, duration, 0.3, 1.0);
    }


    @Override
    public void fade(Node node) {
        fade(node, Duration.millis(300));
    }


    @Override
    public void scale(Node node, Duration duration, double from, double to) {
        ScaleTransition scaleIn = new ScaleTransition(duration, node);
        scaleIn.setFromX(from);
        scaleIn.setFromY(from);
        scaleIn.setToX(to);
        scaleIn.setToY(to);
        scaleIn.play();
    }


    @Override
    public void scale(Node node, Duration duration) {
        scale(node, duration, 0.8, 1.0);
    }


    @Override
    public void scale(Node node) {
        scale(node, Duration.millis(300));
    }
}
