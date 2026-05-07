package com.laventurosa.adapters.ui;

/*import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.application.Platform;*/
import com.laventurosa.usecases.services.Application;
import com.laventurosa.usecases.dto.OperationResult;

public class MainApp {
   
    public static void main(String[] args) {
        Application app = new Application();
        OperationResult result = app.guardarMedicion();
        if (!result.isSuccess()) {
            System.err.println(result.getMessage());
            return;
        }
        System.out.println(result.getMessage());
    }
}
