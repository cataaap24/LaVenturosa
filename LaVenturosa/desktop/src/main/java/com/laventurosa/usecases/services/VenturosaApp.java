package com.laventurosa.usecases.services;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.infrastructure.cache.*;
import com.laventurosa.infrastructure.repositories.*;
import com.laventurosa.infrastructure.services.PdfReporteService;
import com.laventurosa.usecases.dto.*;
import com.laventurosa.usecases.ports.*;

import java.time.OffsetDateTime;
import java.util.List;

public class VenturosaApp {
    private MedicionRepository medicionRepository;
    private MedicionCache cacheMedicionRepository;
    private ConfiguracionAlarmaRepository configuracionAlarmaRepository;
    private ConfiguracionAlarmaCache cacheConfiguracionAlarma;
    private UmbralRepository umbralRepository;
    private UmbralCache cacheUmbralRepository;
    private ReporteService reporteService;

    private ConsultarHistorialUseCase consultarHistorialUseCase;
    private VisualizarEstadoLagunaUseCase visualizarEstadoLagunaUseCase;
    private ConfigurarUmbralesUseCase configurarUmbralesUseCase;
    private GenerarReporteUseCase generarReporteUseCase;
    private AgregarNuevaConfiguracionAlarmaUseCase agregarNuevaConfiguracionAlarmaUseCase;
    private ModificarEstadoConfiguracionAlarmaUseCase modificarEstadoConfiguracionAlarmaUseCase;
    private EliminarConfiguracionAlarmaUseCase eliminarConfiguracionAlarmaUseCase;
    private ConsultarUmbralUseCase consultarUmbralActual;

    public VenturosaApp() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        this.medicionRepository = new PostgresMedicionRepository();
        this.cacheMedicionRepository = new MedicionCache(medicionRepository);
        this.configuracionAlarmaRepository = new PostgresConfiguracionAlarmaRepository();
        this.cacheConfiguracionAlarma = new ConfiguracionAlarmaCache(configuracionAlarmaRepository);
        this.umbralRepository = new PostgresUmbralRepository();
        this.cacheUmbralRepository = new UmbralCache(umbralRepository);
        this.reporteService = new PdfReporteService();

        this.visualizarEstadoLagunaUseCase = new VisualizarEstadoLagunaUseCase(cacheMedicionRepository);
        this.consultarHistorialUseCase = new ConsultarHistorialUseCase(cacheMedicionRepository);
        this.configurarUmbralesUseCase = new ConfigurarUmbralesUseCase(cacheUmbralRepository);
        this.generarReporteUseCase = new GenerarReporteUseCase(cacheMedicionRepository, reporteService);
        this.agregarNuevaConfiguracionAlarmaUseCase = new AgregarNuevaConfiguracionAlarmaUseCase(cacheConfiguracionAlarma);
        this.modificarEstadoConfiguracionAlarmaUseCase = new ModificarEstadoConfiguracionAlarmaUseCase(cacheConfiguracionAlarma);
        this.eliminarConfiguracionAlarmaUseCase = new EliminarConfiguracionAlarmaUseCase(cacheConfiguracionAlarma);
        this.consultarUmbralActual= new ConsultarUmbralUseCase(cacheUmbralRepository);

        System.out.println("[APP] Venturosa System inicializado con éxito.");
    }

    public OperationResult<EstadoLagunaDTO> consultarEstadoActual(String puntoMonitoreo) {
        if (visualizarEstadoLagunaUseCase == null) {
            return OperationResult.fail("El sistema no está conectado a la base de datos.");
        }
        return visualizarEstadoLagunaUseCase.execute(puntoMonitoreo);
    }

    public OperationResult<List<MedicionDTO>> obtenerHistorial(OffsetDateTime desde, OffsetDateTime hasta) {
        if (consultarHistorialUseCase == null) {
            return OperationResult.fail("El caso de uso de historial no está inicializado.");
        }
        return consultarHistorialUseCase.execute(desde, hasta);
    }

    public List<ConfiguracionAlarmaDTO> obtenerConfiguracionesDeAlarma() {
        return configuracionAlarmaRepository.listarTodas().
                                                stream().
                                                map(ConfiguracionAlarmaDTO::new).
                                                toList();
    }

    public OperationResult configurarUmbrales(String punto, String variable, double minC, double minA, double maxA, double maxC) {
        return configurarUmbralesUseCase.execute(punto, variable, minC, minA, maxA, maxC);
    }

    public OperationResult generarReportePDF(String ruta, OffsetDateTime desde, OffsetDateTime hasta) {
        return generarReporteUseCase.execute(ruta, desde, hasta);
    }

    public OperationResult<ConfiguracionAlarmaDTO> agregarNuevaConfiguracionAlarma(String email, String nivel_notificacion) {
        return agregarNuevaConfiguracionAlarmaUseCase.execute(email, nivel_notificacion);
    }

    public OperationResult<ConfiguracionAlarmaDTO> modificarEstadoConfiguracionAlarmaExistente(String email, String nivel_notificacion, boolean nuevoEstado) {
        return modificarEstadoConfiguracionAlarmaUseCase.execute(email, nivel_notificacion, nuevoEstado);
    }

    public OperationResult eliminarConfiguracionAlarma(String email) {
        return eliminarConfiguracionAlarmaUseCase.execute(email);
    }

    public OperationResult<UmbralDTO> obtenerUmbralesActuales(String punto, String variable) {
        return consultarUmbralActual.execute(punto,variable);
    }

    public void limpiarCacheMediciones() {
        cacheMedicionRepository.limpiarCache();
    }

    /** Confirmar para uso
    public void apagarSistema() {
        DatabaseConfig.cerrarConexion();
    }
     **/

}
