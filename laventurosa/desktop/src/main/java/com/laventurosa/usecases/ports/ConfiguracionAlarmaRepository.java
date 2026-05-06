package com.laventurosa.usecases.ports;

import com.laventurosa.entities.ConfiguracionAlarma;
import java.util.List;

public interface ConfiguracionAlarmaRepository {
    ConfiguracionAlarma guardar(ConfiguracionAlarma config);
    List<ConfiguracionAlarma> listarTodas();
    void eliminar(Long id);
}
