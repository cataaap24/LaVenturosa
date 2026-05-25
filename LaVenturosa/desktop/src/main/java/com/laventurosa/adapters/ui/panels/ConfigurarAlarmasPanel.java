package com.laventurosa.adapters.ui.panels;

import com.laventurosa.adapters.ui.utils.AppAware;
import com.laventurosa.adapters.ui.utils.UIUtils;
import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.services.VenturosaApp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfigurarAlarmasPanel implements AppAware {

    @FXML
    private Button btnAbrirAgregar;
    @FXML
    private TableView<ConfiguracionAlarma> tablaCorreos;
    @FXML
    private TableColumn<ConfiguracionAlarma, String> colCorreo;
    @FXML
    private TableColumn<ConfiguracionAlarma, String> colFrecuencia;
    @FXML
    private TableColumn<ConfiguracionAlarma, Boolean> colActivo;
    @FXML
    private TableColumn<ConfiguracionAlarma, Void> colAcciones;
    private ObservableList<ConfiguracionAlarma> observableList = FXCollections.observableArrayList();
    private VenturosaApp app;

    @Override
    public void setApp(VenturosaApp app) {
        this.app = app;
        cargarDatos();
    }

    private void cargarDatos() {
        observableList.setAll(app.obtenerConfiguracionesDeAlarma());
    }

    @FXML
    public void initialize() {

        colCorreo.setCellValueFactory(new PropertyValueFactory<>("emailDestinatario"));
        colFrecuencia.setCellValueFactory(new PropertyValueFactory<>("nivelNotificacion"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colActivo.setCellFactory(col -> new TableCell<ConfiguracionAlarma, Boolean>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(activo ? "Habilitado" : "Deshabilitado");
                }
            }
        });
        colAcciones.setCellFactory(col -> new TableCell<ConfiguracionAlarma, Void>() {
            private final Button btn = new Button();

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ConfiguracionAlarma config = getTableView().getItems().get(getIndex());

                    if (config.isActivo()) {
                        btn.setText("Deshabilitar");
                        btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    } else {
                        btn.setText("Habilitar");
                        btn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                    }

                    btn.setOnAction(e -> {
                        String email = config.getEmailDestinatario();
                        String nivel_notificacion = String.valueOf(config.getNivelNotificacion());
                        boolean estadoActual = config.isActivo();
                        OperationResult<ConfiguracionAlarma> result = app.modificarEstadoConfiguracionAlarmaExistente(email, nivel_notificacion, !estadoActual);
                        if (result.isSuccess()) {
                            config.setActivo(!estadoActual);
                            tablaCorreos.refresh();
                        } else {
                            UIUtils.showError("Error al modificar el estado", null, result.getMessage());
                        }
                    });

                    setGraphic(btn);
                }
            }
        });
        tablaCorreos.setItems(observableList);
    }

    @FXML
    public void agregarCorreo(ActionEvent event) {
        //Crear ventana emergente con el formulario
        Stage form = new Stage();
        form.setTitle("Agregar Correo Notificación");
        form.initModality(Modality.APPLICATION_MODAL);
        form.initOwner(btnAbrirAgregar.getScene().getWindow());
        form.setResizable(false);

        Label lblCorreo = new Label("Correo destinatario");
        lblCorreo.setStyle("-fx-font-weight: bold;");

        //Campo para el correo
        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("example@gmail.com");
        txtCorreo.setStyle("-fx-background-color: #D6E4F0; -fx-background-radius: 5;");
        txtCorreo.setPrefWidth(250.0);

        //Campo para la frecuencia de notificación
        Label lblNivelNotificacion = new Label("Frecuencia de notificación");
        lblNivelNotificacion.setStyle("-fx-font-weight: bold;");

        ToggleGroup grupo = new ToggleGroup();

        RadioButton rbTodas = new RadioButton("Enviar correo en cada alerta");
        rbTodas.setToggleGroup(grupo);
        rbTodas.setSelected(true);

        RadioButton rbCriticas = new RadioButton("Solo alertas críticas");
        rbCriticas.setToggleGroup(grupo);

        //Confirmar guardado
        Button btnConfirmar = new Button("Agregar correo");
        btnConfirmar.setOnAction(e -> {
            String correo = txtCorreo.getText().trim();
            String nivelNotificacion = rbTodas.isSelected() ? "ADVERTENCIA_Y_CRITICO" : "SOLO_CRITICO";

            if (correo.isEmpty()) {
                 txtCorreo.setPromptText("Ingresa un correo!");
                 return;
            }

            OperationResult<ConfiguracionAlarma> result = app.agregarNuevaConfiguracionAlarma(correo, nivelNotificacion);
            if (result.isSuccess()) {
                UIUtils.showInfo("Guardado exitoso", null, result.getMessage());
            } else {
                UIUtils.showError("Error de guardado", null, result.getMessage());
            }
            observableList.setAll(app.obtenerConfiguracionesDeAlarma());
            form.close();
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                lblCorreo,
                txtCorreo,
                lblNivelNotificacion,
                rbTodas,
                rbCriticas,
                btnConfirmar
        );

        form.setScene(new Scene(layout));
        form.showAndWait();
    }
}
