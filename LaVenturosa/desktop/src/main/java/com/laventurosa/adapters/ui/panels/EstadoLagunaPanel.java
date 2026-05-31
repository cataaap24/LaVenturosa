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
import java.util.TreeSet;
import java.util.Set;

public class EstadoLagunaPanel implements AppAware {

    @FXML private Label lblZona1;
    @FXML private Label lblPH1;
    @FXML private Label lblEstadoPH1;

    @FXML private Label lblZona2;
    @FXML private Label lblPH2;
    @FXML private Label lblEstadoPH2;

    @FXML private Label lblZona3;
    @FXML private Label lblPH3;
    @FXML private Label lblEstadoPH3;

    @FXML private LineChart<String, Number> grafica72Horas;
    @FXML private CategoryAxis ejeXTiempo;

    private VenturosaApp app;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private final java.time.ZoneId zonaColombia = java.time.ZoneId.of("America/Bogota");

    private static final String ZONA_ENTRADA = "Laguna-Entrada";
    private static final String ZONA_PRODUCCION = "Laguna-Produccion";
    private static final String ZONA_CANIO = "Laguna-Canio";

    @Override
    public void setApp(VenturosaApp app) {
        this.app = app;
        if (this.app != null) {
            cargarDatosReales();
        }
    }

    private void cargarDatosReales() {
        grafica72Horas.getData().clear();

        OperationResult<EstadoLagunaDTO> resEntrada = app.consultarEstadoActual(ZONA_ENTRADA);
        OperationResult<EstadoLagunaDTO> resProduccion = app.consultarEstadoActual(ZONA_PRODUCCION);
        OperationResult<EstadoLagunaDTO> resCanio = app.consultarEstadoActual(ZONA_CANIO);

        actualizarTarjeta(lblZona1, lblPH1, lblEstadoPH1, "Laguna - Entrada", resEntrada);
        actualizarTarjeta(lblZona2, lblPH2, lblEstadoPH2, "Laguna - Producción", resProduccion);
        actualizarTarjeta(lblZona3, lblPH3, lblEstadoPH3, "Laguna - Caño", resCanio);

        Set<String> etiquetasTiempoOrdenadas = new TreeSet<>();

        agregarCurvaGrafica("Sensor 1 (Entrada)", resEntrada, etiquetasTiempoOrdenadas);
        agregarCurvaGrafica("Sensor 2 (Producción)", resProduccion, etiquetasTiempoOrdenadas);
        agregarCurvaGrafica("Sensor 3 (Caño)", resCanio, etiquetasTiempoOrdenadas);

        ejeXTiempo.setCategories(FXCollections.observableArrayList(etiquetasTiempoOrdenadas));
    }

    private void agregarCurvaGrafica(String nombreVisibleCurva, OperationResult<EstadoLagunaDTO> result, Set<String> etiquetasTiempo) {
        if (!result.isSuccess() || result.getData().getHistorialReciente() == null) return;

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName(nombreVisibleCurva);

        for (MedicionDTO medicion : result.getData().getHistorialReciente()) {
            String fechaFormateada = medicion.getFechaHora().atZoneSameInstant(zonaColombia).format(formatter);

            etiquetasTiempo.add(fechaFormateada);

            serie.getData().add(new XYChart.Data<>(fechaFormateada, medicion.getValor()));
        }

        if (!serie.getData().isEmpty()) {
            grafica72Horas.getData().add(serie);
        }
    }

    private void actualizarTarjeta(Label lblZona, Label labelValor, Label labelEstado, String tituloVisual, OperationResult<EstadoLagunaDTO> result) {
        if (result.isSuccess() && result.getData() != null) {
            EstadoLagunaDTO data = result.getData();

            lblZona.setText(tituloVisual);
            labelValor.setText(String.format("%.2f pH", data.getValor()));
            labelEstado.setText("Estado: " + data.getEstado());

            switch (String.valueOf(data.getEstado()).toUpperCase()) {
                case "CRITICO":
                    labelEstado.setTextFill(Color.RED);
                    break;
                case "ADVERTENCIA":
                    labelEstado.setTextFill(Color.ORANGE);
                    break;
                default:
                    labelEstado.setTextFill(Color.web("#27ae60")); // Verde éxito
                    break;
            }
        } else {
            lblZona.setText(tituloVisual);
            labelValor.setText("--");
            labelEstado.setText("Sin Datos");
            labelEstado.setTextFill(Color.GRAY);
        }
    }
}