package com.laventurosa.usecases.services;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.usecases.dto.ConfiguracionAlarmaDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ModificarEstadoConfiguracionAlarmaUseCaseTest {

    private ModificarEstadoConfiguracionAlarmaUseCase modificarEstadoUseCase;
    private ConfiguracionAlarmaRepository configuracionAlarmaRepositoryMock;

    @BeforeEach
    public void setUp() {
        configuracionAlarmaRepositoryMock = mock(ConfiguracionAlarmaRepository.class);
        modificarEstadoUseCase = new ModificarEstadoConfiguracionAlarmaUseCase(configuracionAlarmaRepositoryMock);
    }

    @Test
    public void execute_CorreoNoExiste_LanzaExcepcionYRetornaFail() {
        String email = "noexiste@laventurosa.com";
        String nivel = "SOLO_CRITICO";
        boolean nuevoEstado = true;

        when(configuracionAlarmaRepositoryMock.obtenerConfiguracionAlarma(email)).thenReturn(null);

        OperationResult<ConfiguracionAlarmaDTO> resultado = modificarEstadoUseCase.execute(email, nivel, nuevoEstado);

        assertFalse(resultado.isSuccess());
        assertTrue(resultado.getMessage().contains("Error al actualizar el correo"));
        assertNull(resultado.getData());
        
        verify(configuracionAlarmaRepositoryMock, never()).guardar(any());
    }

    @Test
    public void execute_ErrorAlGuardarEnRepositorio() {
        String email = "alertas@laventurosa.com";
        String nivel = "SOLO_CRITICO";
        boolean nuevoEstado = false;

        ConfiguracionAlarma configExistente = new ConfiguracionAlarma(email, ConfiguracionAlarma.NivelNotificacion.SOLO_CRITICO);
        when(configuracionAlarmaRepositoryMock.obtenerConfiguracionAlarma(email)).thenReturn(configExistente);
        
        when(configuracionAlarmaRepositoryMock.guardar(any(ConfiguracionAlarma.class))).thenReturn(null);

        OperationResult<ConfiguracionAlarmaDTO> resultado = modificarEstadoUseCase.execute(email, nivel, nuevoEstado);

        assertFalse(resultado.isSuccess());
        assertEquals("Error actualizando el correo", resultado.getMessage());
        assertNull(resultado.getData());
        
        verify(configuracionAlarmaRepositoryMock, times(1)).guardar(configExistente);
    }

    @Test
    public void execute_ActualizacionExitosa() {
        String email = "soporte@laventurosa.com";
        String nivel = "ADVERTENCIA_Y_CRITICO";
        boolean nuevoEstado = true;

        ConfiguracionAlarma configExistente = new ConfiguracionAlarma(email, ConfiguracionAlarma.NivelNotificacion.SOLO_CRITICO);
        configExistente.setActivo(false);

        when(configuracionAlarmaRepositoryMock.obtenerConfiguracionAlarma(email)).thenReturn(configExistente);
        
        when(configuracionAlarmaRepositoryMock.guardar(any(ConfiguracionAlarma.class))).thenReturn(configExistente);

        OperationResult<ConfiguracionAlarmaDTO> resultado = modificarEstadoUseCase.execute(email, nivel, nuevoEstado);

        assertTrue(resultado.isSuccess());
        assertEquals("Correo actualizado correctamente", resultado.getMessage());

        ConfiguracionAlarmaDTO dto = resultado.getData();
        assertNotNull(dto);
        assertEquals(email, dto.getEmailDestinatario());
    }
}