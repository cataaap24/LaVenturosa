package com.laventurosa.usecases.ports;

import com.laventurosa.entities.Medicion;

import java.util.List;

public interface ReporteService {
    public void generarReporteMediciones(List<Medicion> mediciones, String rutaSalida);
}

