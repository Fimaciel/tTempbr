package com.exemplo;

import com.exemplo.service.OshiHardwareService;
import com.exemplo.ui.view.MainView;
import com.exemplo.viewmodel.MonitorViewModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point da aplicação.
 * Responsabilidade única: instanciar dependências e conectar as camadas.
 */
public class TempMonitorApp extends Application {

    private MonitorViewModel viewModel;

    @Override
    public void start(Stage primaryStage) {
        var hardwareService = new OshiHardwareService();
        viewModel = new MonitorViewModel(hardwareService);

        var mainView = new MainView(viewModel);

        Scene scene = new Scene(mainView.build(), 600, 500);
        primaryStage.setTitle("Monitor de Temperatura do Sistema");
        primaryStage.setScene(scene);
        primaryStage.show();

        viewModel.startPolling();
    }

    @Override
    public void stop() {
        if (viewModel != null) {
            viewModel.stopPolling();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
