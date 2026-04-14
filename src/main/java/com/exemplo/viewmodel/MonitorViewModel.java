package com.exemplo.viewmodel;

import com.exemplo.model.SystemSnapshot;
import com.exemplo.service.HardwareService;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ViewModel do padrão MVVM.
 * Expõe dados observáveis (JavaFX Properties) para a View se vincular.
 * Gerencia o ciclo de polling dos sensores.
 */
public class MonitorViewModel {

    private static final int UPDATE_INTERVAL_SECONDS = 2;

    private final HardwareService hardwareService;
    private final ScheduledExecutorService scheduler;

    private final ObjectProperty<SystemSnapshot> snapshot = new SimpleObjectProperty<>();
    private final StringProperty processorInfo = new SimpleStringProperty();

    public MonitorViewModel(HardwareService hardwareService) {
        this.hardwareService = hardwareService;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "sensor-poller");
            t.setDaemon(true);
            return t;
        });
        this.processorInfo.set(hardwareService.getProcessorInfo());
    }

    public void startPolling() {
        // Primeira leitura síncrona na thread chamadora para popular a UI imediatamente
        refresh();
        // Leituras subsequentes: hardware na thread de background, UI update via Platform.runLater
        scheduler.scheduleAtFixedRate(() -> {
            try {
                SystemSnapshot snap = hardwareService.snapshot();
                Platform.runLater(() -> snapshot.set(snap));
            } catch (Exception e) {
                System.err.println("Erro ao ler sensores: " + e.getMessage());
            }
        }, UPDATE_INTERVAL_SECONDS, UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    public void stopPolling() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(3, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void refresh() {
        try {
            snapshot.set(hardwareService.snapshot());
        } catch (Exception e) {
            System.err.println("Erro ao ler sensores: " + e.getMessage());
        }
    }

    public ReadOnlyObjectProperty<SystemSnapshot> snapshotProperty() {
        return snapshot;
    }

    public ReadOnlyStringProperty processorInfoProperty() {
        return processorInfo;
    }
}
