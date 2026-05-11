package com.laventurosa.usecases.services;

import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.dto.ReporteDTO;
import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.usecases.ports.ReporteService;

import java.time.OffsetDateTime;
import java.util.List;

public class GenerarReporteUseCase {
    private final MedicionRepository medicionRepository;
    private final ReporteService reporteService;

    public GenerarReporteUseCase(MedicionRepository medicionRepository, ReporteService reporteService) {
        this.medicionRepository = medicionRepository;
        this.reporteService = reporteService;
    }

    public OperationResult<ReporteDTO> execute(String ruta, OffsetDateTime desde, OffsetDateTime hasta) {
        if (ruta == null || ruta.isBlank()) {
            return OperationResult.fail("Error, ruta invalida");
        }
        try {
            List<Medicion> mediciones;

            if (desde == null && hasta == null) {
                mediciones = medicionRepository.obtenerUltimoMes();
            }
            else if (desde != null && hasta != null) {
                mediciones = medicionRepository.obtenerPorRango(desde, hasta);
            } else {
                return OperationResult.fail("Error, datos de fecha inválidos");
            }
            reporteService.generarReporteMediciones(mediciones, ruta);
            return OperationResult.ok("Reporte generado correctamente");
        } catch (Exception e) {
            return OperationResult.fail("Error al generar el reporte:" + e.getMessage());
        }
    }
}
