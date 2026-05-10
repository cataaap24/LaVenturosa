package com.laventurosa.adapters.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainView {

    @FXML
    private StackPane contentArea;

    @FXML
    private void mostrarEstado() {
        cargarFXML("/fxml/panels/EstadoLagunaPanel.fxml");
    }

    @FXML
    private void mostrarHistorial() {
        cargarFXML("/fxml/panels/HistorialPanel.fxml");
    }

    @FXML
    private void mostrarReportes() {
        cargarFXML("/fxml/panels/GenerarReportePanel.fxml");
    }

    @FXML
    private void mostrarUmbrales() {
        cargarFXML("/fxml/panels/UmbralesDeVariablesPanel.fxml");
    }

    @FXML
    private void mostrarAlarmas() {
        cargarFXML("/fxml/panels/ConfigurarAlarmasPanel.fxml");
    }

    private void cargarFXML(String rutaFXML) {
        try {
            var recurso = getClass().getResource(rutaFXML);
            if (recurso == null) {
                System.err.println("OJO! No se encontro el archivo en: " + rutaFXML);
                return;
            }

            FXMLLoader loader = new FXMLLoader(recurso);
            Parent root = loader.load();

            contentArea.getChildren().setAll(root);

        } catch (IOException e) {
            System.err.println("Error técnico al cargar: " + rutaFXML);
            e.printStackTrace();
        }
    }
}