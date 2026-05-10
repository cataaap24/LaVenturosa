package com.laventurosa.usecases.services;

import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.dto.OperationResult;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

public class MedicionRepoTestingUseCase {
    private MedicionRepository medicionRepository;

    public MedicionRepoTestingUseCase(MedicionRepository medicionRepository) {
        this.medicionRepository = medicionRepository;
    }

    //This function is for check the data is correct and test that the queries was successful
    private static void imprimirMediciones(List<Medicion> mediciones) {
        System.out.println("\n╔══════╦══════════════╦════════╦════════════════════════╦══════════════╦══════════════╗");
        System.out.printf( "║ %-4s ║ %-12s ║ %-6s ║ %-22s ║ %-12s ║ %-12s ║%n",
                "ID", "VARIABLE", "VALOR", "FECHA/HORA (COT)", "ESTADO", "PUNTO");
        System.out.println("╠══════╬══════════════╬════════╬════════════════════════╬══════════════╬══════════════╣");
        for (Medicion m : mediciones) {
            OffsetDateTime cot = m.getFechaHora().withOffsetSameInstant(ZoneOffset.of("-05:00"));
            System.out.printf("║ %-4d ║ %-12s ║ %-6.2f ║ %-22s ║ %-12s ║ %-12s ║%n",
                    m.getId(),
                    m.getVariable().getNombre(),
                    m.getValor(),
                    cot.toString().replace("T", " ").substring(0, 19),
                    m.getEstado().name(),
                    m.getPuntoMonitoreo()
            );
        }

        System.out.println("╚══════╩══════════════╩════════╩════════════════════════╩══════════════╩══════════════╝");
        System.out.println("  Total: " + mediciones.size() + " registros");
    }

    //If we need to insert a new 'Medicion' record, the function can be like this
    // (I guess that this function will never be used, but this is an example)
    public OperationResult executeSQLInsertInjection(Medicion medicion) {
        if (medicion == null) {
            return OperationResult.fail("No se encontró la medición");
        }
        Medicion medicion_guardada = medicionRepository.guardar(medicion);
        if (medicion_guardada == null) {
            return OperationResult.fail("Error al guardar la medicion");
        }

        return OperationResult.ok("Medicion guardada correctamente");
    }

    //If we need to execute 'GenerarReporte' usecase, the function can be like this (and combine with PDF manager)
    public OperationResult executeLastMonthQuery() {
        List<Medicion> mediciones = medicionRepository.obtenerUltimoMes();
        if (mediciones == null || mediciones.isEmpty()) {
            return OperationResult.fail("Lista vacia.");
        }
        System.out.println("Consulta Ultimo Mes");
        imprimirMediciones(mediciones);
        return OperationResult.ok("Consulta realizada exitosamente");
    }

    //If we need to execute 'ConsultarHistorial' usecase, the function can be like this
    public OperationResult executeByDatetimeQuery(OffsetDateTime desde,  OffsetDateTime hasta) {
        List<Medicion> mediciones = medicionRepository.obtenerPorRango(desde, hasta);
        if (mediciones == null || mediciones.isEmpty()) {
            return OperationResult.fail("Lista vacia.");
        }
        System.out.println("Consulta Por Rango");
        imprimirMediciones(mediciones);
        return OperationResult.ok("Consulta realizada exitosamente");
    }

    //If we need to obtain the last measurement by monitoring point, the function can be like this
    public OperationResult executeLastPerMonitoringPointQuery(String puntoMonitoreo) {
        Optional<Medicion> medicion = medicionRepository.obtenerUltimaPorPunto(puntoMonitoreo);
        if (medicion.isEmpty()) {
            return OperationResult.fail("Medicion no obtenida");
        }
        System.out.println("Consulta Ultima Medicion Por Punto");
        Medicion med = medicion.get();
        OffsetDateTime cot = med.getFechaHora().withOffsetSameInstant(ZoneOffset.of("-05:00"));
        System.out.printf("Última medición en %s → %s | %.2f | %s | %s%n",
                med.getPuntoMonitoreo(),
                cot.toString().replace("T", " ").substring(0, 19),
                med.getValor(),
                med.getVariable().getNombre(),
                med.getEstado().name()
        );
        return OperationResult.ok("Consulta realizada exitosamente");
    }

    //If we need to obtain the measurements by Datetime range and monitoring point, the function can be like this
    public OperationResult executeByDatetimeAndMonitoringPointQuery(OffsetDateTime desde, OffsetDateTime hasta, String puntoMonitoreo) {
        List<Medicion> mediciones = medicionRepository.obtenerPorRangoYPunto(desde, hasta, puntoMonitoreo);
        if (mediciones == null || mediciones.isEmpty()) {
            return OperationResult.fail("Lista vacía");
        }
        System.out.println("Consulta Por rango de fechas y punto de monitoreo");
        imprimirMediciones(mediciones);
        return OperationResult.ok("Consulta realizada exitosamente");
    }

    //If we need to obtain the measurements by Datetime range and variable name, the function can be like this
    public OperationResult executeByDatetimeAndVariableQuery(OffsetDateTime desde, OffsetDateTime hasta, String variable) {
        List<Medicion> mediciones = medicionRepository.obtenerPorRangoYVariable(desde, hasta, variable);
        if (mediciones == null || mediciones.isEmpty()) {
            return OperationResult.fail("Lista vacía");
        }
        System.out.println("Consulta Por rango de fechas y variable");
        imprimirMediciones(mediciones);
        return OperationResult.ok("Consulta realizada exitosamente");
    }
}