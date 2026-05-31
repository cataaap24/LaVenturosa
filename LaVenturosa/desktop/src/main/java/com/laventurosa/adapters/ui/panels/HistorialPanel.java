package com.laventurosa.adapters.ui.panels;

import javafx.event.ActionEvent;

import com.laventurosa.usecases.dto.MedicionDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.services.VenturosaApp;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.laventurosa.adapters.ui.utils.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalTime;

public class HistorialPanel implements AppAware {

    @FXML
    private DatePicker fechaInicio;
    @FXML
    private DatePicker fechaFin;
    @FXML
    private TableView<MedicionDTO> tablaHistorial;
    @FXML
    private TableColumn<MedicionDTO, OffsetDateTime> colFecha;
    @FXML
    private TableColumn<MedicionDTO, String> colPuntoMonitoreo;
    @FXML
    private TableColumn<MedicionDTO, String> colVariable;
    @FXML
    private TableColumn<MedicionDTO, Double> colValue;
    @FXML
    private TableColumn<MedicionDTO, String> colEstado;

    @FXML
    private VenturosaApp app;
    private final ObservableList<MedicionDTO> observableList = FXCollections.observableArrayList();

    @Override
    public void setApp(VenturosaApp app) {
        this.app = app;
    }

    @FXML
    public void initialize() {
        tablaHistorial.setItems(observableList);
        colFecha.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFechaHora()));
        colPuntoMonitoreo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPuntoMonitoreo()));
        colVariable.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVariable()));
        colValue.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValor()));
        colEstado.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEstado().getEtiqueta()));
    }

    @FXML
    void consultarHistorial(ActionEvent event) {
        if (fechaInicio.getValue() == null || fechaFin.getValue() == null) {
            UIUtils.showError("Error de fechas", null, "Los campos de fecha no deben estar vacíos");
            return;
        }
    
        // Usamos el offset del sistema o mantén UTC si tu backend lo centraliza así
        ZoneOffset zoneOffset = ZoneOffset.UTC; 
    
        // Ajustamos las horas para cubrir los días completos seleccionados
        OffsetDateTime fechaInicioDT = OffsetDateTime.of(fechaInicio.getValue(), LocalTime.MIN, zoneOffset);
        OffsetDateTime fechaFinDT = OffsetDateTime.of(fechaFin.getValue(), LocalTime.MAX, zoneOffset);

        OperationResult<List<MedicionDTO>> result = app.obtenerHistorial(fechaInicioDT, fechaFinDT);
        if (!result.isSuccess()) {
            UIUtils.showError("Error obteniendo los datos", null, result.getMessage());
            return;
        }
    
        List<MedicionDTO> mediciones = result.getData();
        observableList.setAll(mediciones);
    }
}
