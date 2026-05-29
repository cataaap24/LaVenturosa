package com.laventurosa.usecases.services;

import com.laventurosa.entities.Umbral;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.UmbralRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurarUmbralesUseCaseTest {
    private ConfigurarUmbralesUseCase configurarUmbralesUseCase;
    private UmbralRepository umbralRepositoryMock;

    @BeforeEach
    public void setUp() {
        umbralRepositoryMock = mock(UmbralRepository.class);
        configurarUmbralesUseCase = new ConfigurarUmbralesUseCase(umbralRepositoryMock);
    }

    @Test
    public void execute_ParametrosCumplenLasReglas() {
        String punto = "GLOBAL";
        String nombreVar = "pH";
        double minC = 6.0;
        double minA = 6.5;
        double maxA = 8.5;
        double maxC = 9.0;

        OperationResult resultado = configurarUmbralesUseCase.execute(punto, nombreVar, minC, minA, maxA, maxC);

        assertTrue(resultado.isSuccess());
        assertEquals("Umbrales actualizados con éxito.", resultado.getMessage());

        verify(umbralRepositoryMock, times(1)).guardar(any(Umbral.class));
    }

    @Test
    public void execute_LosUmbralesSonIncoherentes() {
        String punto = "GLOBAL";
        String nombreVar = "pH";
        double minC = 7.0;
        double minA = 6.5;
        double maxA = 8.5;
        double maxC = 9.0;

        OperationResult resultado = configurarUmbralesUseCase.execute(punto, nombreVar, minC, minA, maxA, maxC);

        assertFalse(resultado.isSuccess());
        assertTrue(resultado.getMessage().contains("Datos de configuración inválidos"));

        verify(umbralRepositoryMock, never()).guardar(any(Umbral.class));
    }

    @Test
    public void execute_LaVariableNoExiste() {
        String punto = "GLOBAL";
        String nombreVar = "Inexistente";
        double minC = 6.0;
        double minA = 6.5;
        double maxA = 8.5;
        double maxC = 9.0;

        OperationResult resultado = configurarUmbralesUseCase.execute(punto, nombreVar, minC, minA, maxA, maxC);

        assertFalse(resultado.isSuccess());
        assertTrue(resultado.getMessage().contains("Datos de configuración inválidos"));

        verify(umbralRepositoryMock, never()).guardar(any(Umbral.class));
    }
    }



