package com.laventurosa.adapters.ui;


import com.laventurosa.usecases.services.ImplementationTesting;
import com.laventurosa.infrastructure.repositories.PostgresMedicionRepository;
import com.laventurosa.infrastructure.repositories.PostgresUmbralRepository;
import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.usecases.ports.UmbralRepository;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainView {
    private ImplementationTesting implementationTesting;

    private VBox catalogBox;
    private VBox cartBox;
    private Label totalLabel;

    public MainView() {
        MedicionRepository medicionRepository = new PostgresMedicionRepository();
        UmbralRepository umbralRepository = new PostgresUmbralRepository();

        catalogBox = new VBox(10);
        cartBox = new VBox(10);
        totalLabel = new Label("Total: $ 0.0");
    }
}