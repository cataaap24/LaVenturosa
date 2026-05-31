package com.laventurosa.adapters.ui.panels;

import com.laventurosa.adapters.ui.utils.AppAware;
import com.laventurosa.adapters.ui.utils.UIUtils;
import com.laventurosa.usecases.dto.ConfiguracionAlarmaDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.services.VenturosaApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfigurarAlarmasPanel implements AppAware {

    @FXML
    private Button btnAbrirAgregar;
    @FXML
    private TableView<ConfiguracionAlarmaDTO> tablaCorreos;
    @FXML
    private TableColumn<ConfiguracionAlarmaDTO, String> colCorreo;
    @FXML
    private TableColumn<ConfiguracionAlarmaDTO, String> colFrecuencia;
    @FXML
    private TableColumn<ConfiguracionAlarmaDTO, Boolean> colActivo;
    @FXML
    private TableColumn<ConfiguracionAlarmaDTO, Void> colAcciones;
    
    private ObservableList<ConfiguracionAlarmaDTO> observableList = FXCollections.observableArrayList();
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
        
        colActivo.setCellFactory(col -> new TableCell<ConfiguracionAlarmaDTO, Boolean>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setText(null);
                } else {
                    setText(activo ? "Habilitado" : "Deshabilitado");
                }
            }
        });

        colAcciones.setCellFactory(col -> new TableCell<ConfiguracionAlarmaDTO, Void>() {
            private final Button btnEstado = new Button();
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox contenedor = new HBox(10);

            {
                btnEstado.setPrefWidth(110.0);
                btnEliminar.setPrefWidth(110.0);

                btnEliminar.setStyle("-fx-background-color: #7d7d7d; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold;");
                btnEstado.setStyle("-fx-background-radius: 5; -fx-font-weight: bold;");
                
                contenedor.setAlignment(Pos.CENTER);
                contenedor.getChildren().addAll(btnEstado, btnEliminar);

                btnEliminar.setOnAction(e -> {
                    ConfiguracionAlarmaDTO config = getTableView().getItems().get(getIndex());
                    OperationResult result = app.eliminarConfiguracionAlarma(config.getEmailDestinatario()); 
                    
                    if (result.isSuccess()) {
                        getTableView().getItems().remove(config);
                        UIUtils.showInfo("Eliminado", null, "Configuración eliminada correctamente");
                    } else {
                        UIUtils.showError("Error al eliminar", null, result.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    ConfiguracionAlarmaDTO config = getTableView().getItems().get(getIndex());

                    if (config.isActivo()) {
                        btnEstado.setText("Deshabilitar");
                        btnEstado.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold;");
                    } else {
                        btnEstado.setText("Habilitar");
                        btnEstado.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-weight: bold;");
                    }

                    btnEstado.setOnAction(e -> {
                        String email = config.getEmailDestinatario();
                        String nivel = config.getNivelNotificacion();
                        boolean estadoActual = config.isActivo();
                        
                        OperationResult result = app.modificarEstadoConfiguracionAlarmaExistente(email, nivel, !estadoActual);
                        if (result.isSuccess()) {
                            config.setActivo(!estadoActual);
                            getTableView().refresh(); 
                        } else {
                            UIUtils.showError("Error al modificar el estado", null, result.getMessage());
                        }
                    });
                    
                    setGraphic(contenedor);
                }
            }
        });

        tablaCorreos.setItems(observableList);
    }

    @FXML
    public void agregarCorreo(ActionEvent event) {
        Stage form = new Stage();
        form.setTitle("Agregar Correo Notificación");
        form.initModality(Modality.APPLICATION_MODAL);
        form.initOwner(btnAbrirAgregar.getScene().getWindow());
        form.setResizable(false);

        Label lblCorreo = new Label("Correo destinatario");
        lblCorreo.setStyle("-fx-font-weight: bold;");

        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("example@gmail.com");
        txtCorreo.setStyle("-fx-background-color: #D6E4F0; -fx-background-radius: 5;");
        txtCorreo.setPrefWidth(250.0);

        Label lblNivelNotificacion = new Label("Frecuencia de notificación");
        lblNivelNotificacion.setStyle("-fx-font-weight: bold;");

        ToggleGroup grupo = new ToggleGroup();

        RadioButton rbTodas = new RadioButton("Enviar correo en cada alerta");
        rbTodas.setToggleGroup(grupo);
        rbTodas.setSelected(true);

        RadioButton rbCriticas = new RadioButton("Solo alertas críticas");
        rbCriticas.setToggleGroup(grupo);

        Button btnConfirmar = new Button("Agregar correo");
        btnConfirmar.setOnAction(e -> {
            String correo = txtCorreo.getText().trim();
            String nivelNotificacion = rbTodas.isSelected() ? "ADVERTENCIA_Y_CRITICO" : "SOLO_CRITICO";

            if (correo.isEmpty()) {
                 txtCorreo.setPromptText("Ingresa un correo!");
                 return;
            }

            OperationResult<ConfiguracionAlarmaDTO> result = app.agregarNuevaConfiguracionAlarma(correo, nivelNotificacion);
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
