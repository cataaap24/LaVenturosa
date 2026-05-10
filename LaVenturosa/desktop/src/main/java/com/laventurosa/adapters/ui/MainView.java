package com.laventurosa.adapters.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainView {

    @FXML private Button btnEstado, btnHistorial, btnReportes, btnUmbrales, btnAlarmas;

    private void resaltarBotonActivo(Button botonActivo) {
        Button[] botones = {btnEstado, btnHistorial, btnReportes, btnUmbrales, btnAlarmas};

        for (Button b : botones) {
            if (b != null) {
                b.getStyleClass().remove("button-active");
            }
        }

        if (botonActivo != null) {
            botonActivo.getStyleClass().add("button-active");
        }
    }

    @FXML
    private StackPane contentArea;

    @FXML
    private void mostrarEstado() {
        resaltarBotonActivo(btnEstado);
        cargarFXML("/fxml/panels/EstadoLagunaPanel.fxml");
    }

    @FXML
    private void mostrarHistorial() {
        resaltarBotonActivo(btnHistorial);
        cargarFXML("/fxml/panels/HistorialPanel.fxml");
    }

    @FXML
    private void mostrarReportes() {
        resaltarBotonActivo(btnReportes);
        cargarFXML("/fxml/panels/GenerarReportePanel.fxml");
    }

    @FXML
    private void mostrarUmbrales() {
        resaltarBotonActivo(btnUmbrales);
        cargarFXML("/fxml/panels/UmbralesDeVariablesPanel.fxml");
    }

    @FXML
    private void mostrarAlarmas() {
        resaltarBotonActivo(btnAlarmas);
        cargarFXML("/fxml/panels/ConfigurarAlarmasPanel.fxml");
    }

    @FXML
    public void initialize() {
        mostrarEstado();
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