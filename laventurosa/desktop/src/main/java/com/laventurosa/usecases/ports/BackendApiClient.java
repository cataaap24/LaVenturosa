package com.laventurosa.usecases.ports;

import com.laventurosa.usecases.dto.MedicionDTO;

import java.util.List;


//Contrato para obtener mediciones desde el backend

public interface BackendApiClient {

    List<MedicionDTO> obtenerUltimasMediciones();
    List<MedicionDTO> obtenerHistorial(String desde, String hasta);
}
