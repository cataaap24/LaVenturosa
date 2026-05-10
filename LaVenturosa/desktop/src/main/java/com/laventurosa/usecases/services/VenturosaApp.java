package com.laventurosa.usecases.services;

import com.laventurosa.entities.Medicion;
import com.laventurosa.infrastructure.repositories.PostgresConfiguracionAlarmaRepository;
import com.laventurosa.infrastructure.repositories.PostgresMedicionRepository;
import com.laventurosa.infrastructure.repositories.PostgresUmbralRepository;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;
import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.usecases.ports.UmbralRepository;
import com.laventurosa.infrastructure.config.DatabaseConfig;

import java.time.OffsetDateTime;
import java.util.List;

public class VenturosaApp {
    private MedicionRepository medicionRepository;
    private ConfiguracionAlarmaRepository configuracionAlarmaRepository;
    private UmbralRepository umbralRepository;

    private ConsultarHistorialUseCase consultarHistorialUseCase;
    private VisualizarEstadoLagunaUseCase visualizarEstadoLagunaUseCase;
    private ConfigurarUmbralesUseCase configurarUmbralesUseCase;

    public VenturosaApp() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        this.medicionRepository = new PostgresMedicionRepository();
        this.configuracionAlarmaRepository = new PostgresConfiguracionAlarmaRepository();
        this.umbralRepository = new PostgresUmbralRepository();

        this.visualizarEstadoLagunaUseCase = new VisualizarEstadoLagunaUseCase(medicionRepository);
        this.consultarHistorialUseCase = new ConsultarHistorialUseCase(medicionRepository);
        this.configurarUmbralesUseCase = new ConfigurarUmbralesUseCase(umbralRepository);

        System.out.println("[APP] Venturosa System inicializado con éxito.");
    }

    public OperationResult consultarEstadoActual(String puntoMonitoreo) {
        if (visualizarEstadoLagunaUseCase == null) {
            return OperationResult.fail("El sistema no está conectado a la base de datos.");
        }
        return visualizarEstadoLagunaUseCase.execute(puntoMonitoreo);
    }

    /** Confirmar para uso
    public OperationResult registrarNuevaMedicion(Medicion medicion) {
        if (medicionRepository == null) {
            return OperationResult.fail("Repositorio de datos no disponible.");
        }

        Medicion guardada = medicionRepository.guardar(medicion);
        if (guardada != null) {
            return OperationResult.ok("Medición almacenada correctamente en Supabase.");
        }
        return OperationResult.fail("Error al intentar guardar la medición.");
    }
     **/

    public List<Medicion> obtenerHistorial (OffsetDateTime desde, OffsetDateTime hasta){
        return consultarHistorialUseCase.execute(desde, hasta);
    }

    public OperationResult configurarUmbrales(String punto, String variable, double minC, double minA, double maxA, double maxC) {
        return configurarUmbralesUseCase.execute(punto, variable, minC, minA, maxA, maxC);
    }

    /** Confirmar para uso
    public Optional<Umbral> obtenerUmbralesActuales(String punto, String variable) {
        return umbralRepository.obtenerPorPuntoYVariable(punto, variable);
    }
     **/

    /** Confirmar para uso
    public void apagarSistema() {
        DatabaseConfig.cerrarConexion();
    }
     **/
}