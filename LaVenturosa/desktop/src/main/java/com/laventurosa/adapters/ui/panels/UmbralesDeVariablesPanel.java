package com.laventurosa.adapters.ui.panels;

import com.laventurosa.usecases.services.VenturosaApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class UmbralesDeVariablesPanel {

    private VenturosaApp app;

    @FXML
    private Button GuardarVarFQ_button;
    @FXML
    private ChoiceBox<?> choiceVarFQ;
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
    void GuardarVarFQ_event(ActionEvent event) {
        double v_maxAdver = maxAdver.getText();
        label_varFQ.setText("Hola Estoy Probando");
    }

    public void setApp(VenturosaApp app) {
        this.app = app;
    }
}
