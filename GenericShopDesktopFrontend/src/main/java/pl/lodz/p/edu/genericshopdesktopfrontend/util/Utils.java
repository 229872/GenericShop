package pl.lodz.p.edu.genericshopdesktopfrontend.util;

import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;

public class Utils {

    private Utils() {
        throw new UnsupportedOperationException("Can't instantiate static class.");
    }

    public static void setUpDividers(SplitPane splitPane) {
        double[] dividerPositions = splitPane.getDividerPositions();
        ObservableList<SplitPane.Divider> dividers = splitPane.getDividers();

        if (dividers.isEmpty()) return;

        dividers.get(0)
            .positionProperty()
            .addListener((observableValue, oldVal, newVal) -> {
                splitPane.setDividerPositions(dividerPositions);
            });
    }
}
