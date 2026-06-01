package com.laventurosa.usecases.services;

import com.laventurosa.entities.Medicion;
import com.laventurosa.entities.EstadoCriticidad;
import com.laventurosa.entities.Variable;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.usecases.ports.ReporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class GenerarReporteUseCaseTest {
    private GenerarReporteUseCase generarReporteUseCase;
    private MedicionRepository medicionRepositoryMock;
    private ReporteService reporteServiceMock;

    @BeforeEach
    public void setUp() {
        medicionRepositoryMock = mock(MedicionRepository.class);
        reporteServiceMock = mock(ReporteService.class);
        generarReporteUseCase = new GenerarReporteUseCase(medicionRepositoryMock, reporteServiceMock);
    }

    @Test
    public void execute_RutaInvalida() {
        OperationResult resultado = generarReporteUseCase.execute("    ", null, null);

        assertFalse(resultado.isSuccess());
        assertEquals("Error: La ruta de destino no puede estar vacía.", resultado.getMessage());
        verifyNoInteractions(medicionRepositoryMock, reporteServiceMock);
    }

    @Test
    public void execute_FechaInicioEsPosteriorAFechaFinal() {
        OffsetDateTime hasta = OffsetDateTime.now(ZoneOffset.UTC).minusDays(2);
        OffsetDateTime desdeIncoherente = hasta.plusDays(1);

        OperationResult resultado = generarReporteUseCase.execute("/reportes/doc.pdf", desdeIncoherente, hasta);

        assertFalse(resultado.isSuccess());
        assertEquals("Error: La fecha de inicio no puede ser posterior a la fecha final.", resultado.getMessage());
        verifyNoInteractions(medicionRepositoryMock, reporteServiceMock);
    }

    @Test
    public void execute_NoSeEncontraronMediciones() {
        when(medicionRepositoryMock.obtenerUltimoMes()).thenReturn(new ArrayList<>());

        OperationResult resultado = generarReporteUseCase.execute("/reportes/doc.pdf", null, null);

        assertFalse(resultado.isSuccess());
        assertEquals("No se encontraron mediciones en el rango seleccionado para generar el reporte.", resultado.getMessage());
        verify(medicionRepositoryMock, times(1)).obtenerUltimoMes();
        verifyNoInteractions(reporteServiceMock);
    }

    @Test
    public void execute_ErrorAlEscribirOGuardarElArchivo() throws Exception {
        OffsetDateTime desde = OffsetDateTime.now(ZoneOffset.UTC).minusDays(2);
        OffsetDateTime hasta = OffsetDateTime.now(ZoneOffset.UTC);
        String ruta = "/root/sin_permisos/doc.pdf";

        List<Medicion> medicionesValidas = new ArrayList<>();
        medicionesValidas.add(new Medicion(1L, Variable.fromNombre("pH"), 7.0, desde, EstadoCriticidad.NORMAL, "Punto_1"));

        when(medicionRepositoryMock.obtenerPorRango(desde, hasta)).thenReturn(medicionesValidas);
        doThrow(new RuntimeException("Disk Error")).when(reporteServiceMock).generarReporteMediciones(medicionesValidas, ruta);

        OperationResult resultado = generarReporteUseCase.execute(ruta, desde, hasta);

        assertFalse(resultado.isSuccess());
        assertEquals("Error interno: El sistema no pudo escribir o guardar el archivo del reporte.", resultado.getMessage());
    }

    @Test
    public void execute_ReporteGeneradoExitosamente() throws Exception {
        String ruta = "/reportes/reporte_ok.pdf";
        OffsetDateTime desde = OffsetDateTime.of(2026, 5, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime hasta = OffsetDateTime.of(2026, 5, 30, 23, 59, 0, 0, ZoneOffset.UTC);

        List<Medicion> medicionesValidas = new ArrayList<>();
        medicionesValidas.add(new Medicion(1L, Variable.fromNombre("OxigenoDisuelto"), 6.5, desde.plusDays(5), EstadoCriticidad.NORMAL, "Punto_Laguna"));

        when(medicionRepositoryMock.obtenerPorRango(desde, hasta)).thenReturn(medicionesValidas);

        OperationResult resultado = generarReporteUseCase.execute(ruta, desde, hasta);

        assertTrue(resultado.isSuccess());
        assertEquals("Reporte generado correctamente.", resultado.getMessage());
        verify(medicionRepositoryMock, times(1)).obtenerPorRango(desde, hasta);
        verify(reporteServiceMock, times(1)).generarReporteMediciones(medicionesValidas, ruta);
    }
}
