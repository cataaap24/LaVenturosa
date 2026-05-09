package com.laventurosa.adapters.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));

            Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setTitle("Sistema de Monitoreo - La Venturosa");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar el archivo FXML. Revisa la ruta.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}