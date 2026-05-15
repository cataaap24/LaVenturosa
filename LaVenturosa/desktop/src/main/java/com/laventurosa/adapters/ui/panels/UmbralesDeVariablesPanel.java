package com.laventurosa.adapters.ui.panels;

import com.laventurosa.usecases.services.VenturosaApp;
import com.laventurosa.usecases.dto.OperationResult;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class UmbralesDeVariablesPanel {

    private VenturosaApp app;
    private String variableSeleccionada;

    @FXML
    private Button GuardarVarFQ_button;
    @FXML
    private ChoiceBox<String> choiceVarFQ;
    @FXML
    private Label label_varFQ;
    @FXML
    private TextField maxAdver;
    @FXML
    private TextField maxCritico;
    @FXML
    private TextField minAdver;
    @FXML
    private TextField minCritico;

    @FXML
    public void initialize() {
        choiceVarFQ.getItems().addAll("pH", "Oxígeno disuelto");
        choiceVarFQ.setValue("pH");

        choiceVarFQ.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        variableSeleccionada = newValue;
                        System.out.println("Variable seleccionada: " + variableSeleccionada);
                    }
                }
        );
    }

    @FXML
    void GuardarVarFQ_event(ActionEvent event) {
        double v_minCritico, v_minAdver, v_maxAdver, v_maxCritico;
        try {
            v_minCritico = Double.parseDouble(minCritico.getText().trim());
            v_minAdver   = Double.parseDouble(minAdver.getText().trim());
            v_maxAdver   = Double.parseDouble(maxAdver.getText().trim());
            v_maxCritico = Double.parseDouble(maxCritico.getText().trim());

        } catch (NumberFormatException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de entrada");
            alerta.setHeaderText("Formato de número inválido");
            alerta.setContentText("Por favor, asegúrate de ingresar solo números decimales (ej: 10.5) en todos los campos.");
            alerta.showAndWait();
            return;
        }
        variableSeleccionada = choiceVarFQ.getValue();
        OperationResult<?> resultado = app.configurarUmbrales("GLOBAL",variableSeleccionada,v_minCritico,v_minAdver,v_maxAdver,v_maxCritico);

        Alert alert = new Alert(resultado.isSuccess() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setContentText(resultado.getMessage());
        alert.show();
    }

    public void setApp(VenturosaApp app) {
        this.app = app;
    }
}
