package com.laventurosa.adapters.ui;

/*import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.application.Platform;*/
import com.laventurosa.usecases.services.ImplementationTesting;
import com.laventurosa.usecases.dto.OperationResult;

import java.time.LocalDate;
import java.time.ZoneId;

public class MainApp {
   
    public static void main(String[] args) {
        ImplementationTesting testing = new ImplementationTesting();
        //ZoneId col = ZoneId.of("America/Bogota");

        //Testing al repositorio de mediciones (Confirmado - Funcional)

        /*testing.consultarUltimoMes();
        testing.consultarUltimaPorPunto("Laguna-Canio");
        testing.consultarPorRango(
                LocalDate.of(2026, 5, 8).atStartOfDay(col).toOffsetDateTime(),
                LocalDate.of(2026, 5, 8).atTime(23, 59, 59).atZone(col).toOffsetDateTime()
        );
        testing.consultarPorRangoYPunto(
                LocalDate.of(2026, 5, 1).atStartOfDay(col).toOffsetDateTime(),
                LocalDate.of(2026, 5, 8).atTime(23, 59, 59).atZone(col).toOffsetDateTime(),
                "Laguna-Entrada"
        );
        testing.consultarPorRangoYVariable(
                LocalDate.of(2026, 5, 1).atStartOfDay(col).toOffsetDateTime(),
                LocalDate.of(2026, 5, 8).atTime(23, 59, 59).atZone(col).toOffsetDateTime(),
                "pH"
        );*/

        //Testing al repositorio de umbrales (Confirmado - Funcional)

        // Confirmar UPSERT (insert Oxigeno + update pH)
        testing.testUpsertUmbrales();

        // Consultar uno por variable
        testing.consultarUmbralPorVariable("pH");
        testing.consultarUmbralPorVariable("OxigenoDisuelto");

        // Listar todos
        testing.consultarTodosLosUmbrales();
    }
}
