package com.laventurosa.usecases.services;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.infrastructure.cache.*;
import com.laventurosa.infrastructure.repositories.*;
import com.laventurosa.infrastructure.services.PdfReporteService;
import com.laventurosa.usecases.dto.EstadoLagunaDTO;
import com.laventurosa.usecases.dto.MedicionDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.dto.UmbralDTO;
import com.laventurosa.usecases.ports.*;

import java.time.OffsetDateTime;
import java.util.List;

public class VenturosaApp {
    private MedicionRepository medicionRepository;
    private MedicionRepository cacheMedicionRepository;
    private ConfiguracionAlarmaRepository configuracionAlarmaRepository;
    private ConfiguracionAlarmaCache configuracionAlarmaCache;
    private UmbralRepository umbralRepository;
    private UmbralRepository cacheUmbralRepository;
    private ReporteService reporteService;

    private ConsultarHistorialUseCase consultarHistorialUseCase;
    private VisualizarEstadoLagunaUseCase visualizarEstadoLagunaUseCase;
    private ConfigurarUmbralesUseCase configurarUmbralesUseCase;
    private GenerarReporteUseCase generarReporteUseCase;
    private AgregarNuevaConfiguracionAlarmaUseCase agregarNuevaConfiguracionAlarmaUseCase;
    private ModificarEstadoConfiguracionAlarmaUseCase modificarEstadoConfiguracionAlarmaUseCase;
    private ConsultarUmbralActual consultarUmbralActual;

    public VenturosaApp() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        this.medicionRepository = new PostgresMedicionRepository();
        this.cacheMedicionRepository = new MedicionCache(medicionRepository);
        this.configuracionAlarmaRepository = new PostgresConfiguracionAlarmaRepository();
        this.configuracionAlarmaCache = new ConfiguracionAlarmaCache(configuracionAlarmaRepository);
        this.umbralRepository = new PostgresUmbralRepository();
        this.cacheUmbralRepository = new UmbralCache(umbralRepository);
        this.reporteService = new PdfReporteService();

        this.visualizarEstadoLagunaUseCase = new VisualizarEstadoLagunaUseCase(cacheMedicionRepository);
        this.consultarHistorialUseCase = new ConsultarHistorialUseCase(medicionRepository);
        this.configurarUmbralesUseCase = new ConfigurarUmbralesUseCase(umbralRepository);
        this.generarReporteUseCase = new GenerarReporteUseCase(medicionRepository, reporteService);
        this.agregarNuevaConfiguracionAlarmaUseCase = new AgregarNuevaConfiguracionAlarmaUseCase(configuracionAlarmaRepository);
        this.modificarEstadoConfiguracionAlarmaUseCase = new ModificarEstadoConfiguracionAlarmaUseCase(configuracionAlarmaRepository);
        this.consultarUmbralActual= new ConsultarUmbralActual(umbralRepository);

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

    public List<ConfiguracionAlarma> obtenerConfiguracionesDeAlarma() {
        return configuracionAlarmaRepository.listarTodas();
    }


    public OperationResult configurarUmbrales(String punto, String variable, double minC, double minA, double maxA, double maxC) {
        return configurarUmbralesUseCase.execute(punto, variable, minC, minA, maxA, maxC);
    }

    public OperationResult generarReportePDF(String ruta, OffsetDateTime desde, OffsetDateTime hasta) {
        return generarReporteUseCase.execute(ruta, desde, hasta);
    }

    //Implementar dtos en vez de pasar la entidad -Arturo-

    public OperationResult<ConfiguracionAlarma> agregarNuevaConfiguracionAlarma(String email, String nivel_notificacion) {
        return agregarNuevaConfiguracionAlarmaUseCase.execute(email, nivel_notificacion);
    }

    public OperationResult<ConfiguracionAlarma> modificarEstadoConfiguracionAlarmaExistente(String email, String nivel_notificacion, boolean nuevoEstado) {
        return modificarEstadoConfiguracionAlarmaUseCase.execute(email, nivel_notificacion, nuevoEstado);
    }


    public OperationResult<UmbralDTO> obtenerUmbralesActuales(String punto, String variable) {
        return consultarUmbralActual.execute(punto,variable);
    }

    /** Confirmar para uso
    public void apagarSistema() {
        DatabaseConfig.cerrarConexion();
    }
     **/

}
