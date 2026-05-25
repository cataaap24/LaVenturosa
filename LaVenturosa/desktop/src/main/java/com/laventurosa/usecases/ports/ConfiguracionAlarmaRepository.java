package com.laventurosa.usecases.ports;

import com.laventurosa.entities.ConfiguracionAlarma;
import java.util.List;

public interface ConfiguracionAlarmaRepository {
    ConfiguracionAlarma guardar(ConfiguracionAlarma config);
    List<ConfiguracionAlarma> listarTodas();
    ConfiguracionAlarma obtenerConfiguracionAlarma(String email);
    void eliminar(Long id);
}
