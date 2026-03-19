package com.exemplo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TempMonitorApp extends Application {
    private TemperatureReader tempReader;
    private VBox temperatureContainer;
    private Label cpuLoadLabel;
    private Label systemInfoLabel;
    private ScheduledExecutorService scheduler;

    @Override
    public void start(Stage primaryStage) {
        tempReader = new TemperatureReader();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #2b2b2b;");

        Label titleLabel = new Label("Monitor de Temperatura");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#00ff00"));
        titleLabel.setAlignment(Pos.CENTER);

        systemInfoLabel = new Label();
        systemInfoLabel.setFont(Font.font("Arial", 12));
        systemInfoLabel.setTextFill(Color.web("#cccccc"));
        systemInfoLabel.setText("Processador: " + tempReader.getProcessorInfo());
        systemInfoLabel.setWrapText(true);

        cpuLoadLabel = new Label();
        cpuLoadLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        cpuLoadLabel.setTextFill(Color.web("#ffaa00"));

        VBox topBox = new VBox(10, titleLabel, systemInfoLabel, cpuLoadLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(0, 0, 20, 0));

        temperatureContainer = new VBox(10);
        temperatureContainer.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(temperatureContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #2b2b2b; -fx-background-color: #2b2b2b;");


        Label footerLabel = new Label("Atualização automática a cada 2 segundos");
        footerLabel.setFont(Font.font("Arial", 10));
        footerLabel.setTextFill(Color.web("#888888"));
        footerLabel.setAlignment(Pos.CENTER);

        VBox footerBox = new VBox(footerLabel);
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(10, 0, 0, 0));

        root.setTop(topBox);
        root.setCenter(scrollPane);
        root.setBottom(footerBox);

        updateTemperatures();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(this::updateTemperatures), 2, 2, TimeUnit.SECONDS);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("Monitor de Temperatura do Sistema");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            scheduler.shutdown();
            Platform.exit();
        });
        primaryStage.show();
    }

    private void updateTemperatures() {
        Map<String, Double> temps = tempReader.getTemperatures();
        double cpuLoad = tempReader.getCpuLoad();

        cpuLoadLabel.setText(String.format("Carga da CPU: %.1f%%", cpuLoad));

        temperatureContainer.getChildren().clear();

        for (Map.Entry<String, Double> entry : temps.entrySet()) {
            String name = entry.getKey();
            double value = entry.getValue();

            HBox tempBox = new HBox(10);
            tempBox.setAlignment(Pos.CENTER_LEFT);
            tempBox.setPadding(new Insets(10));
            tempBox.setStyle("-fx-background-color: #3a3a3a; -fx-background-radius: 5;");

            Label nameLabel = new Label(name + ":");
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            nameLabel.setTextFill(Color.web("#ffffff"));
            nameLabel.setPrefWidth(150);

            Label valueLabel = new Label();
            valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            if (value < 0) {
                valueLabel.setText("N/A");
                valueLabel.setTextFill(Color.web("#888888"));
            } else if (name.contains("RPM") || name.contains("Voltage")) {
                valueLabel.setText(String.format("%.0f", value));
                valueLabel.setTextFill(Color.web("#00aaff"));
            } else {
                valueLabel.setText(String.format("%.1f °C", value));

                // Código de cores baseado na temperatura
                if (value < 50) {
                    valueLabel.setTextFill(Color.web("#00ff00")); // Verde
                } else if (value < 70) {
                    valueLabel.setTextFill(Color.web("#ffaa00")); // Laranja
                } else {
                    valueLabel.setTextFill(Color.web("#ff0000")); // Vermelho
                }
            }

            tempBox.getChildren().addAll(nameLabel, valueLabel);
            temperatureContainer.getChildren().add(tempBox);
        }

        if (temps.isEmpty() || temps.values().stream().allMatch(v -> v < 0)) {
            Label warningLabel = new Label("⚠ Sensores de temperatura não disponíveis");
            warningLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            warningLabel.setTextFill(Color.web("#ffaa00"));
            warningLabel.setPadding(new Insets(20));

            Label infoLabel = new Label("Isso pode acontecer se:\n" +
                    "• Você não tem privilégios administrativos\n" +
                    "• Seu hardware não expõe sensores de temperatura\n" +
                    "• Os drivers necessários não estão instalados");
            infoLabel.setFont(Font.font("Arial", 12));
            infoLabel.setTextFill(Color.web("#cccccc"));
            infoLabel.setPadding(new Insets(10));

            temperatureContainer.getChildren().addAll(warningLabel, infoLabel);
        }
    }

    @Override
    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
