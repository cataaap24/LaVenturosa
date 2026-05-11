package com.laventurosa.adapters.ui;

import com.laventurosa.usecases.services.VenturosaApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class MainView {

    private VenturosaApp app;

    public void setApp(VenturosaApp app) {
        this.app = app;
        mostrarEstado();
    }

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

    // PARA IMPLEMENTACION DE EDITAR UMBRALES
    @FXML
    private Button GuardarVarFQ_button;

    @FXML
    private ChoiceBox<?> choiceVarFQ;

    @FXML
    private Label label_varFQ;

    @FXML
    private TextField maxAdver;

    @FXML
    private TextField maxCritico;

    @FXML
    private TextField minAdver;

    @FXML
    private TextField minCritico;

    @FXML
    void GuardarVarFQ_event(ActionEvent event) {
        label_varFQ.setText("hola estoy probando");
    }



    private void cargarFXML(String rutaFXML) {
        try {
            var recurso = getClass().getResource(rutaFXML);
            if (recurso == null) {
                System.err.println("No se encontro el archivo en: " + rutaFXML);
                return;
            }

            FXMLLoader loader = new FXMLLoader(recurso);
            Parent root = loader.load();

            Object controller = loader.getController();

            // AppEstadoLaguna
            if (controller instanceof com.laventurosa.adapters.ui.panels.EstadoLagunaPanel) {
                ((com.laventurosa.adapters.ui.panels.EstadoLagunaPanel) controller).setApp(this.app);
            } else if (controller instanceof com.laventurosa.adapters.ui.panels.HistorialPanel) {
                ((com.laventurosa.adapters.ui.panels.HistorialPanel) controller).setApp(this.app);
            } else if (controller instanceof com.laventurosa.adapters.ui.panels.GenerarReportePanel) {
                ((com.laventurosa.adapters.ui.panels.GenerarReportePanel) controller).setApp(this.app);
            } else if (controller instanceof com.laventurosa.adapters.ui.panels.UmbralesDeVariablesPanel) {
                ((com.laventurosa.adapters.ui.panels.UmbralesDeVariablesPanel) controller).setApp(this.app);
            } else if (controller instanceof com.laventurosa.adapters.ui.panels.ConfigurarAlarmasPanel) {
                ((com.laventurosa.adapters.ui.panels.ConfigurarAlarmasPanel) controller).setApp(this.app);
            }

            contentArea.getChildren().setAll(root);

        } catch (IOException e) {
            System.err.println("Error técnico al cargar: " + rutaFXML);
            e.printStackTrace();
        }


    }
}