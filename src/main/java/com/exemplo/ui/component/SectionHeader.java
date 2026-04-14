package com.exemplo.ui.component;

import com.exemplo.ui.Theme;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Header de seção com título e linha divisória visual.
 */
public class SectionHeader extends HBox {

    public SectionHeader(String icon, String title) {
        setSpacing(10);
        setPadding(new Insets(15, 0, 5, 0));

        Label label = new Label(icon + "  " + title);
        label.setFont(Theme.SECTION_HEADER);
        label.setTextFill(Theme.SECTION_TITLE);

        Region line = new Region();
        line.setStyle(Theme.DIVIDER_STYLE);
        line.setMaxHeight(1);
        HBox.setHgrow(line, Priority.ALWAYS);

        getChildren().addAll(label, line);
        setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    }
}
