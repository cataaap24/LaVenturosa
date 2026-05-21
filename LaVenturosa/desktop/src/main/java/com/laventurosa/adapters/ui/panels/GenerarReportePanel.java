package com.laventurosa.adapters.ui.panels;

import com.laventurosa.usecases.dto.OperationResult;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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
        OffsetDateTime fecha_inicio = null;
        OffsetDateTime fecha_fin = null;
        if (dateInicio.getValue() != null && dateFin.getValue() != null) {
            LocalTime localTime = LocalTime.now();
            ZoneOffset zoneOffset = ZoneOffset.UTC;
            fecha_inicio = OffsetDateTime.of(dateInicio.getValue(), localTime, zoneOffset);
            fecha_fin = OffsetDateTime.of(dateFin.getValue(), localTime, zoneOffset);
        }

        //Configurar ventana emergente de guardado
        FileChooser fc = new FileChooser();
        OffsetDateTime ahora = OffsetDateTime.now();
        fc.setTitle("Guardar reporte PDF");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fechaParseada = ahora.format(formatter);
        fc.setInitialFileName("Reporte laguna " + fechaParseada + ".pdf");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF", "*.pdf");
        fc.getExtensionFilters().add(extFilter);

        //Obtener referencia del archivo a guardar
        File file = fc.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        //Generar reporte PDF
        OperationResult result = app.generarReportePDF(file.getAbsolutePath(), fecha_inicio, fecha_fin);
        if (result.isSuccess()) {
            System.out.println("Reporte generado con éxito");
        } else {
            System.out.println("Problema al generar reporte: " + result.getMessage());
        }
    }
}
