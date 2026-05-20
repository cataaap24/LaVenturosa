package com.laventurosa.adapters.ui.panels;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import javafx.fxml.FXML;
import com.laventurosa.usecases.services.VenturosaApp;

public class GenerarReportePanel {
    private VenturosaApp app;

    @FXML
    private DatePicker dateInicio;

    @FXML
    private DatePicker dateFin;

    public void setApp(VenturosaApp app) {
        this.app = app;
    }

    @FXML
    void generarReporte(ActionEvent event) {
        //Obtener el nodo de la escena para a partir de ella generar la ventana emergente de guardado
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        //Configurar los horarios, se tomará el día mes y año del datePicker pero la hora actual del sistema tanto para fin como para inicio
        LocalTime localTime = LocalTime.now();
        ZoneOffset zoneOffset = ZoneOffset.UTC;
        OffsetDateTime fecha_inicio = OffsetDateTime.of(dateInicio.getValue(), localTime, zoneOffset);
        OffsetDateTime fecha_fin = OffsetDateTime.of(dateFin.getValue(), localTime, zoneOffset);

        //Configurar ventana emergente de guardado
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar reporte PDF");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF", "*.pdf");
        fc.getExtensionFilters().add(extFilter);

        //Obtener referencia del archivo a guardar
        File file = fc.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        //Generar reporte PDF
        app.generarReportePDF(file.getAbsolutePath(), fecha_inicio, fecha_fin);
    }
}
