package com.laventurosa.adapters.ui;

import com.laventurosa.usecases.services.VenturosaApp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private VenturosaApp venturosaApp;

    @Override
    public void init() {
        this.venturosaApp = new VenturosaApp();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();

            MainView mainView = loader.getController();

            if (mainView != null) {
                mainView.setApp(venturosaApp);
            }

            Scene scene = new Scene(root);
            primaryStage.setTitle("VenturosaSystem - Monitoreo de Laguna");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error crítico al iniciar la aplicación.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}