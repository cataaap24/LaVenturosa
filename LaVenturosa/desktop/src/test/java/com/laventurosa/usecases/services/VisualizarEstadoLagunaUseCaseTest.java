package com.laventurosa.usecases.services;

import com.laventurosa.entities.Medicion;
import com.laventurosa.entities.EstadoCriticidad;
import com.laventurosa.entities.Variable;
import com.laventurosa.usecases.dto.EstadoLagunaDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.MedicionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class VisualizarEstadoLagunaUseCaseTest {
    private VisualizarEstadoLagunaUseCase visualizarEstadoLagunaUseCase;
    private MedicionRepository medicionRepositoryMock;

    @BeforeEach
    public void setUp() {
        medicionRepositoryMock = mock(MedicionRepository.class);
        visualizarEstadoLagunaUseCase = new VisualizarEstadoLagunaUseCase(medicionRepositoryMock);
    }

    @Test
    public void execute_LaLagunaNoTieneMediciones() {
        String puntoMonitoreo = "Laguna_Centro";

        when(medicionRepositoryMock.obtenerUltimaPorPunto(puntoMonitoreo)).thenReturn(Optional.empty());

        OperationResult<EstadoLagunaDTO> resultado = visualizarEstadoLagunaUseCase.execute(puntoMonitoreo);

        assertFalse(resultado.isSuccess());
        assertEquals("Sin datos disponibles para el punto: Laguna_Centro", resultado.getMessage());
        assertNull(resultado.getData());

        verify(medicionRepositoryMock, never()).obtenerPorRangoYPunto(any(), any(), anyString());
    }

    @Test
    public void execute_PuntosHistorialExcedenElMaximoDeVeinte() {
        String puntoMonitoreo = "Laguna_Norte";
        OffsetDateTime fechaBase = OffsetDateTime.of(2026, 5, 31, 12, 0, 0, 0, ZoneOffset.UTC);
        Variable variablePh = Variable.fromNombre("pH");

        Medicion ultimaMedicion = new Medicion(1L, variablePh, 7.5, fechaBase, EstadoCriticidad.NORMAL, puntoMonitoreo);
        when(medicionRepositoryMock.obtenerUltimaPorPunto(puntoMonitoreo)).thenReturn(Optional.of(ultimaMedicion));

        List<Medicion> historialSimulado = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            historialSimulado.add(new Medicion((long) i, variablePh, 7.0, fechaBase.minusMinutes(i), EstadoCriticidad.NORMAL, puntoMonitoreo));
        }

        when(medicionRepositoryMock.obtenerPorRangoYPunto(
                fechaBase.minusHours(72),
                fechaBase,
                puntoMonitoreo
        )).thenReturn(historialSimulado);

        OperationResult<EstadoLagunaDTO> resultado = visualizarEstadoLagunaUseCase.execute(puntoMonitoreo);

        assertTrue(resultado.isSuccess());
        assertEquals("Estado de la laguna recuperado exitosamente", resultado.getMessage());

        EstadoLagunaDTO informe = resultado.getData();
        assertNotNull(informe);
        assertEquals(20, informe.getHistorialReciente().size());

        verify(medicionRepositoryMock, times(1)).obtenerUltimaPorPunto(puntoMonitoreo);
    }

    @Test
    public void execute_PuntosHistorialSonMenoresAlMaximo() {
        String puntoMonitoreo = "Laguna_Sur";
        OffsetDateTime fechaBase = OffsetDateTime.of(2026, 5, 31, 15, 0, 0, 0, ZoneOffset.UTC);
        Variable variableOd = Variable.fromNombre("OxigenoDisuelto");

        Medicion ultimaMedicion = new Medicion(10L, variableOd, 5.2, fechaBase, EstadoCriticidad.ADVERTENCIA, puntoMonitoreo);
        when(medicionRepositoryMock.obtenerUltimaPorPunto(puntoMonitoreo)).thenReturn(Optional.of(ultimaMedicion));

        List<Medicion> historialCorto = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            historialCorto.add(new Medicion((long) i, variableOd, 6.0, fechaBase.minusHours(i), EstadoCriticidad.NORMAL, puntoMonitoreo));
        }

        when(medicionRepositoryMock.obtenerPorRangoYPunto(
                fechaBase.minusHours(72),
                fechaBase,
                puntoMonitoreo
        )).thenReturn(historialCorto);

        OperationResult<EstadoLagunaDTO> resultado = visualizarEstadoLagunaUseCase.execute(puntoMonitoreo);

        assertTrue(resultado.isSuccess());
        EstadoLagunaDTO informe = resultado.getData();
        assertNotNull(informe);
        assertEquals(5, informe.getHistorialReciente().size());
    }
}

