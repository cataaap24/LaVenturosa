package com.laventurosa.usecases.services;

import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.usecases.ports.ReporteService;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class GenerarReporteUseCase {
    private final MedicionRepository medicionRepository;
    private final ReporteService reporteService;

    public GenerarReporteUseCase(MedicionRepository medicionRepository, ReporteService reporteService) {
        this.medicionRepository = medicionRepository;
        this.reporteService = reporteService;
    }

    public OperationResult execute(String ruta, OffsetDateTime desde, OffsetDateTime hasta) {
        if (ruta == null || ruta.isBlank()) {
            return OperationResult.fail("Error: La ruta de destino no puede estar vacía.");
        }
        if ((desde == null && hasta != null) || (desde != null && hasta == null)) {
            return OperationResult.fail("Error: Debe especificar ambas fechas (desde/hasta) o ninguna para el último mes.");
        }

        if (desde != null && hasta != null) {
            OffsetDateTime ahora = OffsetDateTime.now(ZoneOffset.UTC);
            if (hasta.isAfter(ahora.plusMinutes(1))) {
                return OperationResult.fail("Error: La fecha final no puede ser superior a la fecha actual.");
            }
            
            if (desde.isAfter(hasta)) {
                return OperationResult.fail("Error: La fecha de inicio no puede ser posterior a la fecha final.");
            }
        }

        List<Medicion> mediciones;
        try {
            if (desde == null && hasta == null) {
                mediciones = medicionRepository.obtenerUltimoMes();
            } else {
                mediciones = medicionRepository.obtenerPorRango(desde, hasta);
            }
        } catch (Exception e) {
            return OperationResult.fail("Error interno: No se pudieron consultar las mediciones de la laguna.");
        }
        if (mediciones.isEmpty()) {
            return OperationResult.fail("No se encontraron mediciones en el rango seleccionado para generar el reporte.");
        }

        try {
            reporteService.generarReporteMediciones(mediciones, ruta);
            return OperationResult.ok("Reporte generado correctamente.");
        } catch (Exception e) {
            return OperationResult.fail("Error interno: El sistema no pudo escribir o guardar el archivo del reporte.");
        }
    }
}
