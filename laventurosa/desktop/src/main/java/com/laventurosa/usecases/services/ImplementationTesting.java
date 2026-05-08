package com.laventurosa.usecases.services;
import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.infrastructure.repositories.PostgresMedicionRepository;
import com.laventurosa.usecases.dto.OperationResult;

import java.time.OffsetDateTime;

public class ImplementationTesting {
    private MedicionRepoTestingUseCase medicionRepoTestingUseCase;
    private MedicionRepository medicionRepository;

    public ImplementationTesting() {
        this.medicionRepository = new PostgresMedicionRepository();
        this.medicionRepoTestingUseCase = new MedicionRepoTestingUseCase(medicionRepository);
    }

    public ImplementationTesting(MedicionRepository medicionRepository) {
        this.medicionRepository = medicionRepository;
        this.medicionRepoTestingUseCase = new MedicionRepoTestingUseCase(medicionRepository);
    }

    // Último mes
    public OperationResult consultarUltimoMes() {
        return medicionRepoTestingUseCase.executeLastMonthQuery();
    }

    // Por rango de fechas
    public OperationResult consultarPorRango(OffsetDateTime desde, OffsetDateTime hasta) {
        return medicionRepoTestingUseCase.executeByDatetimeQuery(desde, hasta);
    }

    // Última medición por punto
    public OperationResult consultarUltimaPorPunto(String puntoMonitoreo) {
        return medicionRepoTestingUseCase.executeLastPerMonitoringPointQuery(puntoMonitoreo);
    }

    // Por rango y punto de monitoreo
    public OperationResult consultarPorRangoYPunto(OffsetDateTime desde, OffsetDateTime hasta, String puntoMonitoreo) {
        return medicionRepoTestingUseCase.executeByDatetimeAndMonitoringPointQuery(desde, hasta, puntoMonitoreo);
    }

    // Por rango y variable
    public OperationResult consultarPorRangoYVariable(OffsetDateTime desde, OffsetDateTime hasta, String variable) {
        return medicionRepoTestingUseCase.executeByDatetimeAndVariableQuery(desde, hasta, variable);
    }
}
