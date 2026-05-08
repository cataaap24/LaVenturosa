package com.laventurosa.usecases.services;

import com.laventurosa.entities.Umbral;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.UmbralRepository;

import java.util.List;
import java.util.Optional;

public class UmbralRepoTestingUseCase {
    private UmbralRepository umbralRepository;

    public UmbralRepoTestingUseCase(UmbralRepository umbralRepository) {
        this.umbralRepository = umbralRepository;
    }

    //This function is for check the data is correct and test that the queries was successful
    private static void imprimirUmbrales(List<Umbral> umbrales) {
        System.out.println("\n╔══════╦══════════════╦══════════════════════╦════════════╦═══════════════╦═══════════════╦════════════╗");
        System.out.printf( "║ %-4s ║ %-12s ║ %-20s ║ %-10s ║ %-13s ║ %-13s ║ %-10s ║%n",
                "ID", "VARIABLE", "PUNTO", "MIN CRIT", "MIN ADVERT", "MAX ADVERT", "MAX CRIT");
        System.out.println("╠══════╬══════════════╬══════════════════════╬════════════╬═══════════════╬═══════════════╬════════════╣");

        for (Umbral u : umbrales) {
            System.out.printf("║ %-4d ║ %-12s ║ %-20s ║ %-10.2f ║ %-13.2f ║ %-13.2f ║ %-10.2f ║%n",
                    u.getId(),
                    u.getVariable().getNombre(),
                    u.getPuntoMonitoreo(),
                    u.getMinCritico(),
                    u.getMinAdvertencia(),
                    u.getMaxAdvertencia(),
                    u.getMaxCritico()
            );
        }

        System.out.println("╚══════╩══════════════╩══════════════════════╩════════════╩═══════════════╩═══════════════╩════════════╝");
        System.out.println("  Total: " + umbrales.size() + " umbrales");
    }

    private static void imprimirUmbral(Umbral u) {
        System.out.println("\n╔══════════════════════╦══════════════════════╗");
        System.out.println( "║ Campo                ║ Valor                ║");
        System.out.println( "╠══════════════════════╬══════════════════════╣");
        System.out.printf( "║ %-20s ║ %-20d ║%n", "ID",             u.getId());
        System.out.printf( "║ %-20s ║ %-20s ║%n", "Variable",       u.getVariable().getNombre());
        System.out.printf( "║ %-20s ║ %-20s ║%n", "Punto Monitoreo",u.getPuntoMonitoreo());
        System.out.printf( "║ %-20s ║ %-20.2f ║%n","Min Crítico",   u.getMinCritico());
        System.out.printf( "║ %-20s ║ %-20.2f ║%n","Min Advertencia",u.getMinAdvertencia());
        System.out.printf( "║ %-20s ║ %-20.2f ║%n","Max Advertencia",u.getMaxAdvertencia());
        System.out.printf( "║ %-20s ║ %-20.2f ║%n","Max Crítico",   u.getMaxCritico());
        System.out.println( "╚══════════════════════╩══════════════════════╝");
    }

    //If we need to insert or update a new 'Umbral' record, the function can be like this
    public OperationResult executeSQLUpsertInjection(Umbral umbral) {
        if (umbral == null) {
            return OperationResult.fail("No se encontró el umbral");
        }

        Umbral umbral_guardado = umbralRepository.guardar(umbral);
        if (umbral_guardado == null) {
            return OperationResult.fail("Error al actualizar el umbral");
        }

        return OperationResult.ok("umbral guardado correctamente");
    }

    //If we need to obtain the threshold by variable's name, the function can be like this
    public OperationResult executeByVariable(String nombreVariable) {
        Optional<Umbral> umbral = umbralRepository.obtenerPorVariable(nombreVariable);
        if (umbral.isEmpty()) {
            return OperationResult.fail("Umbral no encontrado");
        }
        Umbral umb = umbral.get();
        imprimirUmbral(umb);
        return OperationResult.ok("Consulta realizada exitosamente");
    }

    //If we need to obtain all the thresholds of system
    public OperationResult executeAllQuery() {
        List<Umbral> umbrales = umbralRepository.listarTodos();
        if (umbrales == null || umbrales.isEmpty()) {
            return OperationResult.fail("Lista vacía");
        }
        imprimirUmbrales(umbrales);
        return OperationResult.ok("Consulta realizada exitosamente");
    }
}
