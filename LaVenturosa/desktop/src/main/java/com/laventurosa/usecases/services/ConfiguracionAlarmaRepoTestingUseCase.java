package com.laventurosa.usecases.services;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;

import java.util.List;

public class ConfiguracionAlarmaRepoTestingUseCase {
    private ConfiguracionAlarmaRepository configuracionAlarmaRepository;

    public ConfiguracionAlarmaRepoTestingUseCase(ConfiguracionAlarmaRepository configuracionAlarmaRepository) {
        this.configuracionAlarmaRepository = configuracionAlarmaRepository;
    }

    private static void imprimirConfiguraciones(List<ConfiguracionAlarma> configuraciones) {
        System.out.println("\n╔══════╦══════════════════════════════╦══════════════════╦════════╗");
        System.out.printf( "║ %-4s ║ %-28s ║ %-16s ║ %-6s ║%n",
                "ID", "EMAIL", "NIVEL", "ACTIVO");
        System.out.println("╠══════╬══════════════════════════════╬══════════════════╬════════╣");

        for (ConfiguracionAlarma c : configuraciones) {
            System.out.printf("║ %-4d ║ %-28s ║ %-16s ║ %-6s ║%n",
                    c.getId(),
                    c.getEmailDestinatario(),
                    c.getNivelNotificacion().name(),
                    c.isActivo() ? "SI" : "NO"
            );
        }

        System.out.println("╚══════╩══════════════════════════════╩══════════════════╩════════╝");
        System.out.println("  Total: " + configuraciones.size() + " configuraciones");
    }

    // INSERT nueva configuracion
    public OperationResult executeInsert(ConfiguracionAlarma config) {
        if (config == null) {
            return OperationResult.fail("Configuración nula");
        }
        ConfiguracionAlarma guardada = configuracionAlarmaRepository.guardar(config);
        if (guardada == null) {
            return OperationResult.fail("Error al guardar la configuración");
        }
        System.out.println("[INSERT] Configuración guardada → ID: " + guardada.getId()
                + " | " + guardada.getEmailDestinatario()
                + " | " + guardada.getNivelNotificacion().name()
                + " | Activo: " + guardada.isActivo());
        return OperationResult.ok("Configuración guardada correctamente");
    }

    // DELETE por id
    public OperationResult executeEliminar(Long id) {
        configuracionAlarmaRepository.eliminar(id);
        System.out.println("[DELETE] Configuración con ID " + id + " eliminada.");
        return OperationResult.ok("Configuración eliminada correctamente");
    }

    // Listar todas
    public OperationResult executeListarTodas() {
        List<ConfiguracionAlarma> configuraciones = configuracionAlarmaRepository.listarTodas();
        if (configuraciones == null || configuraciones.isEmpty()) {
            return OperationResult.fail("Lista vacía");
        }
        System.out.println("Consulta Todas las Configuraciones de Alarma");
        imprimirConfiguraciones(configuraciones);
        return OperationResult.ok("Consulta realizada exitosamente");
    }
}
