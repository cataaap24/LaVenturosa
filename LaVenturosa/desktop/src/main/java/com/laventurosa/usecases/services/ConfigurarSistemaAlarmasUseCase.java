package com.laventurosa.usecases.services;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;

import java.util.List;
import java.util.regex.Pattern;

public class ConfigurarSistemaAlarmasUseCase {

    // Patrón de expresión regular estándar para validación de emails
    // soluciones en Stack Overflow (Pregunta #201323)
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");

    private final ConfiguracionAlarmaRepository configuracionAlarmaRepository;

    public ConfigurarSistemaAlarmasUseCase(ConfiguracionAlarmaRepository configuracionAlarmaRepository) {
        this.configuracionAlarmaRepository = configuracionAlarmaRepository;
    }

    // Retorna el objeto ConfiguracionAlarma persistido dentro del OperationResult para que la UI tengan acceso inmediato a los datos sin necesidad de hacer una consulta adicional
    public OperationResult<ConfiguracionAlarma> agregar(String email, ConfiguracionAlarma.NivelNotificacion nivel) {
        if (email == null || email.isBlank()){
            return OperationResult.fail("El email no puede estar vacío.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()){
            return OperationResult.fail("El email no tiene un formato válido.");
        }
        ConfiguracionAlarma config = new ConfiguracionAlarma(email.trim(), nivel);
        ConfiguracionAlarma guardada = configuracionAlarmaRepository.guardar(config);
        return OperationResult.ok("Configuración de alarma guardada.", guardada);
    }

    public OperationResult<List<ConfiguracionAlarma>> listar() {
        return OperationResult.ok("Ok", configuracionAlarmaRepository.listarTodas());
    }

    public OperationResult<Void> eliminar(Long id) {
        configuracionAlarmaRepository.eliminar(id);
        return OperationResult.ok("Configuración eliminada.");
    }
}
