package com.laventurosa.usecases.services;

import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.usecases.dto.ConfiguracionAlarmaDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AgregarNuevaConfiguracionAlarmaUseCaseTest {
    private AgregarNuevaConfiguracionAlarmaUseCase agregarNuevaConfiguracionAlarmaUseCase;
    private ConfiguracionAlarmaRepository configuracionAlarmaRepositoryMock;

    @BeforeEach
    public void setUp() {
        configuracionAlarmaRepositoryMock = mock(ConfiguracionAlarmaRepository.class);
        agregarNuevaConfiguracionAlarmaUseCase = new AgregarNuevaConfiguracionAlarmaUseCase(configuracionAlarmaRepositoryMock);
    }

    @Test
    public void execute_CamposVacios() {
        OperationResult<ConfiguracionAlarmaDTO> resultado = agregarNuevaConfiguracionAlarmaUseCase.execute("", "SOLO_CRITICO");

        assertFalse(resultado.isSuccess());
        assertEquals("Campos vacíos", resultado.getMessage());
        assertNull(resultado.getData());
        verifyNoInteractions(configuracionAlarmaRepositoryMock);
    }

    @Test
    public void execute_EmailFormatoInvalido() {
        OperationResult<ConfiguracionAlarmaDTO> resultado = agregarNuevaConfiguracionAlarmaUseCase.execute("correoErroneo.com", "ADVERTENCIA_Y_CRITICO");

        assertFalse(resultado.isSuccess());
        assertEquals("El email no tiene un formato válido.", resultado.getMessage());
        assertNull(resultado.getData());
        verifyNoInteractions(configuracionAlarmaRepositoryMock);
    }

    @Test
    public void execute_CorreoYaExiste() {
        String email = "contacto@laventurosa.com";
        ConfiguracionAlarma configExistente = new ConfiguracionAlarma(email, ConfiguracionAlarma.NivelNotificacion.SOLO_CRITICO);

        when(configuracionAlarmaRepositoryMock.obtenerConfiguracionAlarma(email)).thenReturn(configExistente);

        OperationResult<ConfiguracionAlarmaDTO> resultado = agregarNuevaConfiguracionAlarmaUseCase.execute(email, "SOLO_CRITICO");

        assertFalse(resultado.isSuccess());
        assertEquals("Error: este correo ya existe", resultado.getMessage());
        assertNull(resultado.getData());
        verify(configuracionAlarmaRepositoryMock, never()).guardar(any());
    }

    @Test
    public void execute_NivelNotificacionInvalidoLanzaExcepcion() {
        String email = "alarma@laventurosa.com";
        when(configuracionAlarmaRepositoryMock.obtenerConfiguracionAlarma(email)).thenReturn(null);

        OperationResult<ConfiguracionAlarmaDTO> resultado = agregarNuevaConfiguracionAlarmaUseCase.execute(email, "NIVEL_INVENTADO");

        assertFalse(resultado.isSuccess());
        assertTrue(resultado.getMessage().contains("Error al guardar el correo:"));
        assertNull(resultado.getData());
        verify(configuracionAlarmaRepositoryMock, never()).guardar(any());
    }

    @Test
    public void execute_GuardadoCorrectamente() {
        String email = "alertas@laventurosa.com";
        String nivelInput = "SOLO_CRITICO";

        when(configuracionAlarmaRepositoryMock.obtenerConfiguracionAlarma(email)).thenReturn(null);

        ConfiguracionAlarma configGuardada = new ConfiguracionAlarma(email, ConfiguracionAlarma.NivelNotificacion.SOLO_CRITICO);
        when(configuracionAlarmaRepositoryMock.guardar(any(ConfiguracionAlarma.class))).thenReturn(configGuardada);

        OperationResult<ConfiguracionAlarmaDTO> resultado = agregarNuevaConfiguracionAlarmaUseCase.execute(email, nivelInput);

        assertTrue(resultado.isSuccess());
        assertEquals("Correo agregado correctamente", resultado.getMessage());

        ConfiguracionAlarmaDTO dto = resultado.getData();
        assertNotNull(dto);
        assertEquals(email, dto.getEmailDestinatario());
        assertEquals("Solo críticas", dto.getNivelNotificacion());
        assertTrue(dto.isActivo());

        verify(configuracionAlarmaRepositoryMock, times(1)).guardar(any(ConfiguracionAlarma.class));
    }
}
