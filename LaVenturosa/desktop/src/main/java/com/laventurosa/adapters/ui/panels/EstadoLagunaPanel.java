package com.laventurosa.adapters.ui.panels;

import com.laventurosa.adapters.ui.utils.AppAware;
import com.laventurosa.usecases.services.VenturosaApp;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.dto.EstadoLagunaDTO;
import com.laventurosa.usecases.dto.MedicionDTO;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class EstadoLagunaPanel implements AppAware {

    @FXML private Label lblPH;
    @FXML private Label lblEstadoPH;

    @FXML private Label lblPH1;
    @FXML private Label lblEstadoPH1;

    @FXML private LineChart<String, Number> grafica72Horas;
    @FXML private CategoryAxis ejeXTiempo;
    @FXML private NumberAxis ejeYValores;

    private VenturosaApp app;

    // Formateador para mostrar "Día/Mes Hora:Min" en el eje X de forma limpia
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @Override
    public void setApp(VenturosaApp app) {
        this.app = app;
        // Limpiar el gráfico antes de cargar datos por si se vuelve a renderizar
        grafica72Horas.getData().clear();
        cargarDatosReales();
    }

    private void cargarDatosReales() {
        if (app == null) return;

        // Consultar e inyectar datos del Sensor 1
        actualizarSensor(lblPH, lblEstadoPH, "Sensor 1");

        // Consultar e inyectar datos del Sensor 2
        actualizarSensor(lblPH1, lblEstadoPH1, "Sensor 2");
    }

    private void actualizarSensor(Label labelValor, Label labelEstado, String punto) {
        OperationResult<EstadoLagunaDTO> result = app.consultarEstadoActual(punto);

        if (result.isSuccess()) {
            EstadoLagunaDTO data = result.getData();

            labelValor.setText(data.getValor() + " pH");
            labelEstado.setText("Estado: " + data.getEstado());

            // Cambiar color valores de criticidad
            if (data.getEstado().equals("CRITICO")) {
                labelEstado.setTextFill(Color.RED);
            } else if (data.getEstado().equals("ADVERTENCIA")) {
                labelEstado.setTextFill(Color.ORANGE);
            } else {
                labelEstado.setTextFill(Color.web("#27ae60"));
            }

            // --- Lógica para añadir los datos a la Gráfica ---
            if (data.getHistorialReciente() != null && !data.getHistorialReciente().isEmpty()) {
                agregarSerieAGrafica(punto, data.getHistorialReciente());
            }

        } else {
            labelValor.setText("--");
            labelEstado.setText(result.getMessage());
        }
    }

    /**
     * Toma el historial de mediciones de un sensor y lo proyecta en el gráfico de líneas.
     */
    private void agregarSerieAGrafica(String nombreSensor, List<MedicionDTO> historial) {
        System.out.println("¡Graficando! " + nombreSensor + " tiene " + historial.size() + " mediciones.");
        // Creamos una nueva serie de datos (una línea) para este sensor específico
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName(nombreSensor);

        for (MedicionDTO medicion : historial) {
            // Formateamos la fecha y hora para que no sature el eje X
            String fechaFormateada = medicion.getFechaHora().format(formatter);
            double valor = medicion.getValor();

            // Añadimos el punto al gráfico (X: Tiempo formateado, Y: Valor de la variable)
            serie.getData().add(new XYChart.Data<>(fechaFormateada, valor));
        }

        // Añadir la línea construida a nuestra gráfica de JavaFX
        grafica72Horas.getData().add(serie);
    }
}