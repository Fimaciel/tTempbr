package com.exemplo.ui.component;

import com.exemplo.ui.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Card com barra de progresso visual para CPU, memória e disco.
 */
public class ProgressCard extends VBox {

    public ProgressCard(String name, String detail, double percent) {
        setSpacing(6);
        setPadding(new Insets(10, 12, 10, 12));
        setStyle(Theme.CARD_STYLE);

        // Top row: name + percentage
        Label nameLabel = new Label(name);
        nameLabel.setFont(Theme.BODY_BOLD);
        nameLabel.setTextFill(Theme.TEXT_PRIMARY);

        Label percentLabel = new Label(String.format("%.1f%%", percent));
        percentLabel.setFont(Theme.BODY_BOLD);
        percentLabel.setTextFill(Theme.progressColor(percent));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topRow = new HBox(nameLabel, spacer, percentLabel);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Progress bar
        StackPane progressBar = buildProgressBar(percent);

        // Detail label
        Label detailLabel = new Label(detail);
        detailLabel.setFont(Theme.CAPTION);
        detailLabel.setTextFill(Theme.TEXT_SECONDARY);

        getChildren().addAll(topRow, progressBar, detailLabel);
    }

    private StackPane buildProgressBar(double percent) {
        double clampedPercent = Math.max(0, Math.min(100, percent));

        Region track = new Region();
        track.setPrefHeight(8);
        track.setMaxHeight(8);
        track.setStyle(Theme.PROGRESS_TRACK_STYLE);

        Color barColor = Theme.progressColor(clampedPercent);
        String hex = String.format("#%02x%02x%02x",
                (int) (barColor.getRed() * 255),
                (int) (barColor.getGreen() * 255),
                (int) (barColor.getBlue() * 255));

        Region fill = new Region();
        fill.setPrefHeight(8);
        fill.setMaxHeight(8);
        fill.setMaxWidth(Double.MAX_VALUE);
        fill.setStyle("-fx-background-color: " + hex + "; -fx-background-radius: 4;");

        StackPane bar = new StackPane(track, fill);
        bar.setAlignment(Pos.CENTER_LEFT);

        bar.widthProperty().addListener((obs, old, w) -> {
            double width = w.doubleValue() * (clampedPercent / 100.0);
            fill.setMaxWidth(width);
        });

        return bar;
    }
}
