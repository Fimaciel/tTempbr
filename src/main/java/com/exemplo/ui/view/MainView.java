package com.exemplo.ui.view;

import com.exemplo.model.DiskUsage;
import com.exemplo.model.FormatUtils;
import com.exemplo.model.SensorReading;
import com.exemplo.model.SystemSnapshot;
import com.exemplo.ui.Theme;
import com.exemplo.ui.component.ProgressCard;
import com.exemplo.ui.component.SectionHeader;
import com.exemplo.ui.component.SensorCard;
import com.exemplo.viewmodel.MonitorViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * View principal da aplicação.
 * Organizada em seções claras: Sistema, CPU, Memória, Temperaturas, Ventoinhas, Voltagens, Discos.
 */
public class MainView {

    private final MonitorViewModel viewModel;
    private final VBox contentContainer = new VBox(8);

    public MainView(MonitorViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));
        root.setStyle(Theme.BACKGROUND_STYLE);

        root.setTop(buildHeader());
        root.setCenter(buildCenter());
        root.setBottom(buildFooter());

        viewModel.snapshotProperty().addListener((obs, old, snap) -> {
            if (snap != null) updateDisplay(snap);
        });

        return root;
    }

    private VBox buildHeader() {
        Label title = new Label("tTempbr");
        title.setFont(Theme.TITLE);
        title.setTextFill(Theme.ACCENT);

        Label subtitle = new Label("Monitor de Hardware em Tempo Real");
        subtitle.setFont(Theme.BODY);
        subtitle.setTextFill(Theme.TEXT_SECONDARY);

        VBox header = new VBox(4, title, subtitle);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 12, 0));
        return header;
    }

    private ScrollPane buildCenter() {
        contentContainer.setPadding(new Insets(4));

        ScrollPane scroll = new ScrollPane(contentContainer);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle(Theme.SCROLL_STYLE);
        return scroll;
    }

    private HBox buildFooter() {
        Label footer = new Label("Atualização a cada 2s");
        footer.setFont(Theme.CAPTION);
        footer.setTextFill(Theme.TEXT_FOOTER);

        Label platform = new Label(System.getProperty("os.name") + " | Java " + System.getProperty("java.version"));
        platform.setFont(Theme.CAPTION);
        platform.setTextFill(Theme.TEXT_FOOTER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox box = new HBox(footer, spacer, platform);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 0, 0, 0));
        return box;
    }

    // ---- Update ----

    private void updateDisplay(SystemSnapshot snap) {
        contentContainer.getChildren().clear();

        addSystemInfoSection(snap);
        addCpuSection(snap);
        addMemorySection(snap);
        addTemperatureSection(snap);
        addFanSection(snap);
        addVoltageSection(snap);
        addDiskSection(snap);
    }

    private void addSystemInfoSection(SystemSnapshot snap) {
        contentContainer.getChildren().add(new SectionHeader("\ud83d\udcbb", "Sistema"));

        VBox infoBox = new VBox(6);
        infoBox.setPadding(new Insets(10, 12, 10, 12));
        infoBox.setStyle(Theme.CARD_STYLE);

        infoBox.getChildren().addAll(
                infoRow("Processador", snap.processorName()),
                infoRow("Cores", snap.physicalCores() + " físicos / " + snap.logicalCores() + " lógicos"),
                infoRow("SO", snap.osInfo()),
                infoRow("Uptime", snap.formattedUptime())
        );

        contentContainer.getChildren().add(infoBox);
    }

    private void addCpuSection(SystemSnapshot snap) {
        contentContainer.getChildren().add(new SectionHeader("\u2699\ufe0f", "CPU"));

        contentContainer.getChildren().add(
                new ProgressCard("CPU Total", snap.formattedCpuLoad(), snap.cpuLoadPercent())
        );

        if (snap.perCoreLoads() != null && snap.perCoreLoads().length > 0) {
            FlowPane coreGrid = new FlowPane(8, 8);
            coreGrid.setPadding(new Insets(4, 0, 0, 0));

            for (int i = 0; i < snap.perCoreLoads().length; i++) {
                double load = snap.perCoreLoads()[i];
                Label coreLabel = new Label(String.format("Core %d: %.0f%%", i, load));
                coreLabel.setFont(Theme.CAPTION);
                coreLabel.setTextFill(Theme.progressColor(load));
                coreLabel.setPadding(new Insets(4, 8, 4, 8));
                coreLabel.setStyle(Theme.CARD_STYLE);
                coreGrid.getChildren().add(coreLabel);
            }

            contentContainer.getChildren().add(coreGrid);
        }
    }

    private void addMemorySection(SystemSnapshot snap) {
        contentContainer.getChildren().add(new SectionHeader("\ud83e\udde0", "Memória RAM"));

        contentContainer.getChildren().add(
                new ProgressCard("RAM", snap.formattedMemoryDetail(), snap.memoryUsedPercent())
        );
    }

    private void addTemperatureSection(SystemSnapshot snap) {
        List<SensorReading> temps = snap.temperatures();
        if (temps.isEmpty()) return;

        contentContainer.getChildren().add(new SectionHeader("\ud83c\udf21\ufe0f", "Temperaturas"));

        if (!snap.hasAvailableTemperatures()) {
            showSensorWarning();
            return;
        }

        for (SensorReading reading : temps) {
            contentContainer.getChildren().add(new SensorCard(reading));
        }
    }

    private void addFanSection(SystemSnapshot snap) {
        List<SensorReading> fans = snap.fans();
        if (fans.isEmpty()) return;

        contentContainer.getChildren().add(new SectionHeader("\ud83c\udf00", "Ventoinhas"));

        for (SensorReading reading : fans) {
            contentContainer.getChildren().add(new SensorCard(reading));
        }
    }

    private void addVoltageSection(SystemSnapshot snap) {
        List<SensorReading> voltages = snap.voltages();
        if (voltages.isEmpty()) return;

        contentContainer.getChildren().add(new SectionHeader("\u26a1", "Voltagens"));

        for (SensorReading reading : voltages) {
            contentContainer.getChildren().add(new SensorCard(reading));
        }
    }

    private void addDiskSection(SystemSnapshot snap) {
        if (snap.disks().isEmpty()) return;

        contentContainer.getChildren().add(new SectionHeader("\ud83d\udcbe", "Discos"));

        for (DiskUsage disk : snap.disks()) {
            String name = disk.name() + " (" + disk.mountPoint() + ")";
            String detail = String.format("%s usado de %s",
                    FormatUtils.formatBytes(disk.usedBytes()),
                    FormatUtils.formatBytes(disk.totalBytes()));

            contentContainer.getChildren().add(
                    new ProgressCard(name, detail, disk.usedPercent())
            );
        }
    }

    // ---- Helpers ----

    private HBox infoRow(String label, String value) {
        Label keyLabel = new Label(label);
        keyLabel.setFont(Theme.BODY);
        keyLabel.setTextFill(Theme.TEXT_SECONDARY);
        keyLabel.setMinWidth(100);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Theme.BODY_BOLD);
        valueLabel.setTextFill(Theme.TEXT_PRIMARY);
        valueLabel.setWrapText(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(8, keyLabel, spacer, valueLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void showSensorWarning() {
        Label warning = new Label("\u26a0 Sensores de temperatura não disponíveis");
        warning.setFont(Theme.BODY_BOLD);
        warning.setTextFill(Theme.TEMP_ELEVATED);
        warning.setPadding(new Insets(12));

        Label info = new Label("""
                Isso pode acontecer se:
                \u2022 Você não tem privilégios administrativos
                \u2022 Seu hardware não expõe sensores de temperatura
                \u2022 Os drivers necessários não estão instalados""");
        info.setFont(Theme.BODY);
        info.setTextFill(Theme.TEXT_SECONDARY);
        info.setPadding(new Insets(8));

        VBox box = new VBox(4, warning, info);
        box.setStyle(Theme.CARD_STYLE);
        box.setPadding(new Insets(10));

        contentContainer.getChildren().add(box);
    }
}
