package pl.lodz.p.edu.genericshopdesktopfrontend.service.animation;

import javafx.scene.Node;
import javafx.util.Duration;

public interface AnimationService {

    static AnimationService getInstance() {
        return new AnimationServiceImpl();
    }

    void shake(Node node, Duration duration, int cycleCount, double from, double by);

    void shake(Node node, Duration duration);

    void shake(Node node);


    void fade(Node node, Duration duration, double from, double to);

    void fade(Node node, Duration duration);

    void fade(Node node);


    void scale(Node node, Duration duration, double from, double to);

    void scale(Node node, Duration duration);

    void scale(Node node);
}
