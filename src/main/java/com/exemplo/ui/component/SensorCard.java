package com.exemplo.ui.component;

import com.exemplo.model.SensorReading;
import com.exemplo.ui.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * Componente reutilizável que exibe uma leitura de sensor como card estilizado.
 */
public class SensorCard extends HBox {

    public SensorCard(SensorReading reading) {
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(10, 12, 10, 12));
        setStyle(Theme.CARD_STYLE);

        Label nameLabel = new Label(reading.name());
        nameLabel.setFont(Theme.BODY);
        nameLabel.setTextFill(Theme.TEXT_SECONDARY);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valueLabel = new Label(reading.formattedValue());
        valueLabel.setFont(Theme.BODY_BOLD);
        valueLabel.setTextFill(resolveColor(reading));

        getChildren().addAll(nameLabel, spacer, valueLabel);
    }

    private static Color resolveColor(SensorReading reading) {
        if (!reading.isAvailable()) return Theme.UNAVAILABLE;
        return switch (reading.type()) {
            case TEMPERATURE -> Theme.temperatureColor(reading.value());
            case FAN_SPEED, VOLTAGE -> Theme.INFO_VALUE;
        };
    }
}
