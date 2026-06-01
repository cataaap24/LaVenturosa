package com.laventurosa.usecases.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;

public class EliminarConfiguracionAlarmaUseCaseTest {

    private EliminarConfiguracionAlarmaUseCase eliminarUseCase;
    private ConfiguracionAlarmaRepository configuracionAlarmaRepositoryMock;

    @BeforeEach
    public void setUp() {
        configuracionAlarmaRepositoryMock = mock(ConfiguracionAlarmaRepository.class);
        eliminarUseCase = new EliminarConfiguracionAlarmaUseCase(configuracionAlarmaRepositoryMock);
    }

    @Test
    public void execute_EmailVacio_RetornaFailSinInteracciones() {
        OperationResult<ConfiguracionAlarma> resultado = eliminarUseCase.execute("");

        assertFalse(resultado.isSuccess());
        assertEquals("Campos vacíos", resultado.getMessage());
        assertNull(resultado.getData());
        
        verifyNoInteractions(configuracionAlarmaRepositoryMock);
    }

    @Test
    public void execute_EmailNull_RetornaFailSinInteracciones() {
        OperationResult<ConfiguracionAlarma> resultado = eliminarUseCase.execute(null);

        assertFalse(resultado.isSuccess());
        assertEquals("Campos vacíos", resultado.getMessage());
        assertNull(resultado.getData());
        
        verifyNoInteractions(configuracionAlarmaRepositoryMock);
    }

    @Test
    public void execute_CorreoNoExiste_LanzaExcepcionYRetornaFail() {
        String email = "noexiste@laventurosa.com";

        when(configuracionAlarmaRepositoryMock.obtenerConfiguracionAlarma(email)).thenReturn(null);

        OperationResult<ConfiguracionAlarma> resultado = eliminarUseCase.execute(email);

        assertFalse(resultado.isSuccess());
        assertTrue(resultado.getMessage().contains("Error al eliminar el correo:"));
        assertNull(resultado.getData());

        verify(configuracionAlarmaRepositoryMock, times(1)).obtenerConfiguracionAlarma(email);
        verify(configuracionAlarmaRepositoryMock, never()).eliminar(anyLong());
    }

    @Test
    public void execute_EliminacionExitosa() {
        String email = "remover@laventurosa.com";
        long idSimulado = 42L;

        ConfiguracionAlarma configExistenteMock = mock(ConfiguracionAlarma.class);
        
        when(configExistenteMock.getId()).thenReturn(idSimulado);

        when(configuracionAlarmaRepositoryMock.obtenerConfiguracionAlarma(email)).thenReturn(configExistenteMock);

        OperationResult<ConfiguracionAlarma> resultado = eliminarUseCase.execute(email);

        assertTrue(resultado.isSuccess());
        assertEquals("Correo eliminado correctamente", resultado.getMessage());
        assertNull(resultado.getData());

        verify(configuracionAlarmaRepositoryMock, times(1)).obtenerConfiguracionAlarma(email);
        verify(configuracionAlarmaRepositoryMock, times(1)).eliminar(idSimulado);
    }
}