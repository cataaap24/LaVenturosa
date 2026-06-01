package com.laventurosa.usecases.services;

import com.laventurosa.entities.Medicion;
import com.laventurosa.entities.EstadoCriticidad;
import com.laventurosa.entities.Variable;
import com.laventurosa.usecases.dto.MedicionDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.MedicionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConsultarHistorialUseCaseTest {
    private ConsultarHistorialUseCase consultarHistorialUseCase;
    private MedicionRepository medicionRepositoryMock;

    @BeforeEach
    public void setUp() {
        medicionRepositoryMock = mock(MedicionRepository.class);
        consultarHistorialUseCase = new ConsultarHistorialUseCase(medicionRepositoryMock);
    }

    @Test
    public void execute_FechasSonNulas() {
        // Ejecutamos pasando nulos para forzar la primera validación
        OperationResult<List<MedicionDTO>> resultado = consultarHistorialUseCase.execute(null, null);

        assertFalse(resultado.isSuccess());
        assertEquals("Las fechas de inicio y fin son obligatorias.", resultado.getMessage());
        assertNull(resultado.getData());

        // Verificamos que jamás toque la base de datos/repositorio
        verify(medicionRepositoryMock, never()).obtenerPorRango(any(), any());
    }

    @Test
    public void execute_FechaInicioEsPosteriorAFechaFin() {
        OffsetDateTime hasta = OffsetDateTime.of(2026, 5, 31, 12, 0, 0, 0, ZoneOffset.UTC);
        // Ponemos la fecha 'desde' un día después de 'hasta' para romper la regla
        OffsetDateTime desde = hasta.plusDays(1); 

        OperationResult<List<MedicionDTO>> resultado = consultarHistorialUseCase.execute(desde, hasta);

        assertFalse(resultado.isSuccess());
        assertEquals("La fecha de inicio debe ser anterior a la fecha fin.", resultado.getMessage());
        assertNull(resultado.getData());

        verify(medicionRepositoryMock, never()).obtenerPorRango(any(), any());
    }

    @Test
    public void execute_ParametrosCumplenLasReglasYEncuentraDatos() {
        OffsetDateTime desde = OffsetDateTime.of(2026, 5, 28, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime hasta = OffsetDateTime.of(2026, 5, 31, 12, 0, 0, 0, ZoneOffset.UTC);
        
        Variable variablePh = Variable.fromNombre("pH");
        String punto = "Laguna_Principal";

        // Simulamos que el repositorio devuelve una lista con 2 mediciones
        List<Medicion> medicionesSimuladas = new ArrayList<>();
        medicionesSimuladas.add(new Medicion(1L, variablePh, 7.2, desde.plusHours(5), EstadoCriticidad.NORMAL, punto));
        medicionesSimuladas.add(new Medicion(2L, variablePh, 6.4, desde.plusHours(10), EstadoCriticidad.ADVERTENCIA, punto));

        when(medicionRepositoryMock.obtenerPorRango(desde, hasta)).thenReturn(medicionesSimuladas);

        // Act
        OperationResult<List<MedicionDTO>> resultado = consultarHistorialUseCase.execute(desde, hasta);

        // Assert
        assertTrue(resultado.isSuccess());
        assertEquals("Se encontraron 2 registros.", resultado.getMessage());
        
        List<MedicionDTO> dtos = resultado.getData();
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        
        // Verificamos que el mapeo Clean Arch a DTO se haya hecho correctamente en el primer elemento
        MedicionDTO primerDto = dtos.get(0);
        assertEquals(1L, primerDto.getId());
        assertEquals("pH", primerDto.getVariable());
        assertEquals(7.2, primerDto.getValor());
        assertEquals(EstadoCriticidad.NORMAL, primerDto.getEstado());
        assertEquals(punto, primerDto.getPuntoMonitoreo());

        verify(medicionRepositoryMock, times(1)).obtenerPorRango(desde, hasta);
    }

    @Test
    public void execute_RangoValidoPeroNoHayRegistros() {
        OffsetDateTime desde = OffsetDateTime.of(2026, 5, 28, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime hasta = OffsetDateTime.of(2026, 5, 31, 12, 0, 0, 0, ZoneOffset.UTC);

        // Simulamos rango vacío en la base de datos
        when(medicionRepositoryMock.obtenerPorRango(desde, hasta)).thenReturn(new ArrayList<>());

        OperationResult<List<MedicionDTO>> resultado = consultarHistorialUseCase.execute(desde, hasta);

        assertTrue(resultado.isSuccess());
        assertEquals("Se encontraron 0 registros.", resultado.getMessage());
        assertNotNull(resultado.getData());
        assertTrue(resultado.getData().isEmpty());

        verify(medicionRepositoryMock, times(1)).obtenerPorRango(desde, hasta);
    }
}
