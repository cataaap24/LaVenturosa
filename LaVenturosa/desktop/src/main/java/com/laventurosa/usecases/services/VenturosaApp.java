package com.laventurosa.usecases.services;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.entities.EstadoCriticidad;
import com.laventurosa.entities.Medicion;
import com.laventurosa.entities.Variable;
import com.laventurosa.infrastructure.cache.ConfiguracionAlarmaCache;
import com.laventurosa.infrastructure.cache.EstadoLagunaCache;
import com.laventurosa.infrastructure.repositories.PostgresConfiguracionAlarmaRepository;
import com.laventurosa.infrastructure.repositories.PostgresMedicionRepository;
import com.laventurosa.infrastructure.repositories.PostgresUmbralRepository;
import com.laventurosa.infrastructure.services.PdfReporteService;
import com.laventurosa.usecases.dto.MedicionDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;
import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.usecases.ports.ReporteService;
import com.laventurosa.usecases.ports.UmbralRepository;

import java.time.OffsetDateTime;
import java.util.List;

public class VenturosaApp {
    private MedicionRepository medicionRepository;
    private MedicionRepository cacheMedicionRepository;
    private ConfiguracionAlarmaRepository configuracionAlarmaRepository;
    private ConfiguracionAlarmaCache configuracionAlarmaCache;
    private UmbralRepository umbralRepository;
    private ReporteService reporteService;

    private ConsultarHistorialUseCase consultarHistorialUseCase;
    private VisualizarEstadoLagunaUseCase visualizarEstadoLagunaUseCase;
    private ConfigurarUmbralesUseCase configurarUmbralesUseCase;
    private GenerarReporteUseCase generarReporteUseCase;
    private AgregarNuevaConfiguracionAlarmaUseCase agregarNuevaConfiguracionAlarmaUseCase;
    private ModificarEstadoConfiguracionAlarmaUseCase modificarEstadoConfiguracionAlarmaUseCase;

    public VenturosaApp() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        this.medicionRepository = new PostgresMedicionRepository();
        this.cacheMedicionRepository = new EstadoLagunaCache(medicionRepository);
        this.configuracionAlarmaRepository = new PostgresConfiguracionAlarmaRepository();
        this.configuracionAlarmaCache = new ConfiguracionAlarmaCache(configuracionAlarmaRepository);
        this.umbralRepository = new PostgresUmbralRepository();
        this.reporteService = new PdfReporteService();

        this.visualizarEstadoLagunaUseCase = new VisualizarEstadoLagunaUseCase(cacheMedicionRepository);
        this.consultarHistorialUseCase = new ConsultarHistorialUseCase(medicionRepository);
        this.configurarUmbralesUseCase = new ConfigurarUmbralesUseCase(umbralRepository);
        this.generarReporteUseCase = new GenerarReporteUseCase(medicionRepository, reporteService);
        this.agregarNuevaConfiguracionAlarmaUseCase = new AgregarNuevaConfiguracionAlarmaUseCase(configuracionAlarmaRepository);
        this.modificarEstadoConfiguracionAlarmaUseCase = new ModificarEstadoConfiguracionAlarmaUseCase(configuracionAlarmaRepository);

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
    
    // Luego se delegará correctamente en el panel
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

    public OperationResult<ConfiguracionAlarma> agregarNuevaConfiguracionAlarma(String email, String nivel_notificacion) {
        return agregarNuevaConfiguracionAlarmaUseCase.execute(email, nivel_notificacion);
    }

    public OperationResult<ConfiguracionAlarma> modificarEstadoConfiguracionAlarmaExistente(String email, String nivel_notificacion, boolean nuevoEstado) {
        return modificarEstadoConfiguracionAlarmaUseCase.execute(email, nivel_notificacion, nuevoEstado);
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
