package com.laventurosa.usecases.services;

import com.laventurosa.entities.Umbral;
import com.laventurosa.entities.Variable;
import com.laventurosa.usecases.dto.UmbralDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.UmbralRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConsultarUmbralUseCaseTest {

    private ConsultarUmbralUseCase consultarUmbralUseCase;
    private UmbralRepository umbralRepositoryMock;

    @BeforeEach
    public void setUp() {
        umbralRepositoryMock = mock(UmbralRepository.class);
        consultarUmbralUseCase = new ConsultarUmbralUseCase(umbralRepositoryMock);
    }

    @Test
    public void execute_UmbralNoExiste_RetornaFail() {
        String punto = "Laguna-Entrada";
        String variable = "pH";

        when(umbralRepositoryMock.obtenerPorPuntoYVariable(punto, variable)).thenReturn(Optional.empty());

        OperationResult<UmbralDTO> resultado = consultarUmbralUseCase.execute(punto, variable);

        assertFalse(resultado.isSuccess());
        assertEquals("No se encontró el umbral para el punto: Laguna-Entrada y variable: pH", resultado.getMessage());
        assertNull(resultado.getData());

        verify(umbralRepositoryMock, times(1)).obtenerPorPuntoYVariable(punto, variable);
    }

    @Test
    public void execute_ConsultaExitosa() {
        String punto = "Laguna-Produccion";
        String nombreVar = "Oxigeno";
        long idSimulado = 10L;

        Variable variableMock = mock(Variable.class);
        when(variableMock.getNombre()).thenReturn(nombreVar);

        Umbral umbralMock = mock(Umbral.class);
        when(umbralMock.getId()).thenReturn(idSimulado);
        when(umbralMock.getVariable()).thenReturn(variableMock);
        when(umbralMock.getPuntoMonitoreo()).thenReturn(punto);
        when(umbralMock.getMinCritico()).thenReturn(4.5);
        when(umbralMock.getMinAdvertencia()).thenReturn(5.5);
        when(umbralMock.getMaxAdvertencia()).thenReturn(8.0);
        when(umbralMock.getMaxCritico()).thenReturn(9.0);

        when(umbralRepositoryMock.obtenerPorPuntoYVariable(punto, nombreVar)).thenReturn(Optional.of(umbralMock));

        OperationResult<UmbralDTO> resultado = consultarUmbralUseCase.execute(punto, nombreVar);

        assertTrue(resultado.isSuccess());
        assertEquals("Umbral consultado exitosamente", resultado.getMessage());

        UmbralDTO dto = resultado.getData();
        assertNotNull(dto);
        assertEquals(idSimulado, dto.getId());
        assertEquals(nombreVar, dto.getVariableNombre());
        assertEquals(punto, dto.getPuntoMonitoreo());
        assertEquals(4.5, dto.getMinCritico());
        assertEquals(5.5, dto.getMinAdvertencia());
        assertEquals(8.0, dto.getMaxAdvertencia());
        assertEquals(9.0, dto.getMaxCritico());

        verify(umbralRepositoryMock, times(1)).obtenerPorPuntoYVariable(punto, nombreVar);
    }
}
