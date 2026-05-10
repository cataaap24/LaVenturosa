package com.laventurosa.usecases.ports;

import com.laventurosa.entities.Alerta;
import java.util.List;

public interface AlertaRepository {
    Alerta guardar(Alerta alerta);
    List<Alerta> listarTodas();
}