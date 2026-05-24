package com.laventurosa.adapters.ui.panels;

import com.laventurosa.adapters.ui.utils.AppAware;
import com.laventurosa.usecases.services.VenturosaApp;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.dto.EstadoLagunaDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class EstadoLagunaPanel implements AppAware {

    @FXML private Label lblPH;
    @FXML private Label lblEstadoPH;

    @FXML private Label lblPH1;
    @FXML private Label lblEstadoPH1;

    private VenturosaApp app;

    @Override
    public void setApp(VenturosaApp app) {
        this.app = app;
        // Testeo con datos reales
        // app.insertarDatosDePrueba();
        cargarDatosReales();
    }

    private void cargarDatosReales() {
        if (app == null) return;

        // Consultar Sensor 1
        actualizarSensor(lblPH, lblEstadoPH, "Sensor 1");

        // Consultar Sensor 2
        actualizarSensor(lblPH1, lblEstadoPH1, "Sensor 2");
    }

    private void actualizarSensor(Label labelValor, Label labelEstado, String punto) {
        OperationResult<EstadoLagunaDTO> result = app.consultarEstadoActual(punto);

        if (result.isSuccess()) {
            EstadoLagunaDTO data = result.getData();

            labelValor.setText(data.getValor() + " pH");
            labelEstado.setText("Estado: " + data.getEstado());

            // Cambiar color valores
            if (data.getEstado().equals("CRITICO")) {
                labelEstado.setTextFill(Color.RED);
            } else if (data.getEstado().equals("ADVERTENCIA")) {
                labelEstado.setTextFill(Color.ORANGE);
            } else {
                labelEstado.setTextFill(Color.web("#27ae60"));
            }
        } else {
            labelValor.setText("--");
            labelEstado.setText(result.getMessage());
        }
    }
}