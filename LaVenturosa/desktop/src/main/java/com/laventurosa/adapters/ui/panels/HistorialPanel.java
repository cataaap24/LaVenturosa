package com.laventurosa.adapters.ui.panels;

import com.laventurosa.adapters.ui.utils.AppAware;
import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.services.VenturosaApp;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.OffsetDateTime;

public class HistorialPanel implements AppAware {

    @FXML
    private DatePicker fechaInicio;
    @FXML
    private DatePicker fechaFin;
    @FXML
    private TableView<Medicion> tablaHistorial;
    @FXML
    private TableColumn<Medicion, OffsetDateTime> colFecha;
    @FXML
    private TableColumn<Medicion, String> colPuntoMonitoreo;
    @FXML
    private TableColumn<Medicion, String> colVariable;
    @FXML
    private TableColumn<Medicion, Double> colValue;
    @FXML
    private TableColumn<Medicion, Boolean> colEstado;

    @FXML
    private VenturosaApp app;

    @Override
    public void setApp(VenturosaApp app) {
        this.app = app;
    }


}
