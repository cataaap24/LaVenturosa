package com.laventurosa.adapters.ui.panels;

import com.laventurosa.adapters.ui.utils.AppAware;
import com.laventurosa.usecases.services.VenturosaApp;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.dto.EstadoLagunaDTO;
import com.laventurosa.usecases.dto.MedicionDTO;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EstadoLagunaPanel implements AppAware {

    @FXML private Label lblPH;
    @FXML private Label lblEstadoPH;

    @FXML private Label lblPH1;
    @FXML private Label lblEstadoPH1;

    @FXML private LineChart<String, Number> grafica72Horas;
    @FXML private CategoryAxis ejeXTiempo;

    private VenturosaApp app;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private final java.time.ZoneId zonaColombia = java.time.ZoneId.of("America/Bogota");

    @Override
    public void setApp(VenturosaApp app) {
        this.app = app;
        grafica72Horas.getData().clear();
        cargarDatosReales();
    }

    private void cargarDatosReales() {
        if (app == null) return;

        OperationResult<EstadoLagunaDTO> resSensor1 = app.consultarEstadoActual("Sensor 1");
        OperationResult<EstadoLagunaDTO> resSensor2 = app.consultarEstadoActual("Sensor 2");

        actualizarLabelsTarjeta(lblPH, lblEstadoPH, resSensor1);
        actualizarLabelsTarjeta(lblPH1, lblEstadoPH1, resSensor2);

        List<MedicionDTO> historialGlobalUnificado = new ArrayList<>();

        if (resSensor1.isSuccess() && resSensor1.getData().getHistorialReciente() != null) {
            historialGlobalUnificado.addAll(resSensor1.getData().getHistorialReciente());
        }
        if (resSensor2.isSuccess() && resSensor2.getData().getHistorialReciente() != null) {
            historialGlobalUnificado.addAll(resSensor2.getData().getHistorialReciente());
        }

        Collections.sort(historialGlobalUnificado, (m1, m2) -> m1.getFechaHora().compareTo(m2.getFechaHora()));

        List<String> categoriasEjeX = new ArrayList<>();
        for (MedicionDTO med : historialGlobalUnificado) {
            String fechaFormateada = med.getFechaHora().atZoneSameInstant(zonaColombia).format(formatter);
            if (!categoriasEjeX.contains(fechaFormateada)) {
                categoriasEjeX.add(fechaFormateada);
            }
        }
        ejeXTiempo.setCategories(FXCollections.observableArrayList(categoriasEjeX));

        agregarSeriePorSensor("Sensor 1", historialGlobalUnificado);
        agregarSeriePorSensor("Sensor 2", historialGlobalUnificado);
    }

    private void agregarSeriePorSensor(String nombreSensor, List<MedicionDTO> historialUnificado) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName(nombreSensor);

        int puntosAgregados = 0;

        for (MedicionDTO medicion : historialUnificado) {
            // Condición de seguridad: sólo añadimos el punto si pertenece a este sensor
            if (nombreSensor.equals(medicion.getPuntoMonitoreo())) {
                String fechaFormateada = medicion.getFechaHora().atZoneSameInstant(zonaColombia).format(formatter);
                serie.getData().add(new XYChart.Data<>(fechaFormateada, medicion.getValor()));
                puntosAgregados++;
            }
        }

        if (puntosAgregados > 0) {
            grafica72Horas.getData().add(serie);
        }
    }


    private void actualizarLabelsTarjeta(Label labelValor, Label labelEstado, OperationResult<EstadoLagunaDTO> result) {
        if (result.isSuccess()) {
            EstadoLagunaDTO data = result.getData();
            labelValor.setText(data.getValor() + " pH");
            labelEstado.setText("Estado: " + data.getEstado());

            if ("CRITICO".equals(data.getEstado())) {
                labelEstado.setTextFill(Color.RED);
            } else if ("ADVERTENCIA".equals(data.getEstado())) {
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