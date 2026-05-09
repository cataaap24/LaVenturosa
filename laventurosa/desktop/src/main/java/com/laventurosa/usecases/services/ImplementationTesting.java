package com.laventurosa.usecases.services;
import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.entities.Umbral;
import com.laventurosa.entities.Variable;
import com.laventurosa.infrastructure.repositories.PostgresConfiguracionAlarmaRepository;
import com.laventurosa.infrastructure.repositories.PostgresUmbralRepository;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;
import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.infrastructure.repositories.PostgresMedicionRepository;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.UmbralRepository;

import java.time.OffsetDateTime;

public class ImplementationTesting {
    private MedicionRepoTestingUseCase medicionRepoTestingUseCase;
    private UmbralRepoTestingUseCase umbralRepoTestingUseCase;
    private ConfiguracionAlarmaRepoTestingUseCase configuracionAlarmaRepoTestingUseCase;

    private MedicionRepository medicionRepository;
    private UmbralRepository umbralRepository;
    private ConfiguracionAlarmaRepository configuracionAlarmaRepository;


    public ImplementationTesting() {
        this.medicionRepository = new PostgresMedicionRepository();
        this.medicionRepoTestingUseCase = new MedicionRepoTestingUseCase(medicionRepository);
        this.umbralRepository = new PostgresUmbralRepository();
        this.umbralRepoTestingUseCase = new UmbralRepoTestingUseCase(umbralRepository);
        this.configuracionAlarmaRepository = new PostgresConfiguracionAlarmaRepository();
        this.configuracionAlarmaRepoTestingUseCase = new ConfiguracionAlarmaRepoTestingUseCase(configuracionAlarmaRepository);
    }

    public ImplementationTesting(MedicionRepository medicionRepository) {
        this.medicionRepository = medicionRepository;
        this.medicionRepoTestingUseCase = new MedicionRepoTestingUseCase(medicionRepository);
    }

    //========================== Test for PostgresMedicionRepository ==============================

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

    //========================== Test for PostgresUmbralRepository ==============================

    public OperationResult testUpsertUmbrales() {
        // INSERT — Oxigeno no existe, debe crear
        Umbral oxigeno = new Umbral(null, Variable.fromNombre("OxigenoDisuelto"), "GLOBAL", 2.0, 4.0, 7.0, 9.0);
        OperationResult insertResult = umbralRepoTestingUseCase.executeSQLUpsertInjection(oxigeno);
        System.out.println("[UPSERT INSERT Oxigeno] " + insertResult.getMessage());

        // UPDATE — pH ya existe, debe modificar valores
        Umbral ph = new Umbral(null, Variable.fromNombre("pH"), "GLOBAL", 5.0, 6.0, 8.5, 9.5);
        OperationResult updateResult = umbralRepoTestingUseCase.executeSQLUpsertInjection(ph);
        System.out.println("[UPSERT UPDATE pH]      " + updateResult.getMessage());

        return insertResult.isSuccess() && updateResult.isSuccess()
                ? OperationResult.ok("UPSERT confirmado correctamente")
                : OperationResult.fail("Alguno de los UPSERT falló");
    }

    // Consulta por variable
    public OperationResult consultarUmbralPorVariable(String variable) {
        return umbralRepoTestingUseCase.executeByVariable(variable);
    }

    // Listar todos los umbrales
    public OperationResult consultarTodosLosUmbrales() {
        return umbralRepoTestingUseCase.executeAllQuery();
    }

    //========================= Test for PostgresConfiguracionAlarmaRepository ===================

    public OperationResult testInsertConfiguracion() {
        // INSERT activo
        ConfiguracionAlarma c1 = new ConfiguracionAlarma(null, "admin@laventurosa.com",
                ConfiguracionAlarma.NivelNotificacion.valueOf("SOLO_CRITICO"), true);
        OperationResult r1 = configuracionAlarmaRepoTestingUseCase.executeInsert(c1);
        System.out.println("[INSERT c1] " + r1.getMessage());

        // INSERT inactivo
        ConfiguracionAlarma c2 = new ConfiguracionAlarma(null, "monitor@laventurosa.com",
                ConfiguracionAlarma.NivelNotificacion.valueOf("ADVERTENCIA_Y_CRITICO"), false);
        OperationResult r2 = configuracionAlarmaRepoTestingUseCase.executeInsert(c2);
        System.out.println("[INSERT c2] " + r2.getMessage());

        return OperationResult.ok("Test insert completado");
    }

    public OperationResult testEliminarConfiguracion(Long id) {
        return configuracionAlarmaRepoTestingUseCase.executeEliminar(id);
    }

    public OperationResult consultarTodasLasConfiguraciones() {
        return configuracionAlarmaRepoTestingUseCase.executeListarTodas();
    }
}
