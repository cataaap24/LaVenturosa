package com.laventurosa.usecases.services;

import com.laventurosa.entities.*;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarMedicionUseCaseTest {

    @Mock private MedicionRepository medicionRepository;
    @Mock private UmbralRepository umbralRepository;
    @Mock private AlertaRepository alertaRepository;
    @Mock private ConfiguracionAlarmaRepository alarmaRepository;
    @Mock private NotificacionService notificacion;

    private RegistrarMedicionUseCase useCase;
    private Umbral umbralPH;
    private LocalDateTime timestamp;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarMedicionUseCase(
                medicionRepository, umbralRepository,
                alertaRepository, alarmaRepository, notificacion);

        // normal: 6.5–8.5 , crítico: 4.0–10.0
        umbralPH = new Umbral(Variable.pH(), "Estación-1", 4.0, 6.5, 8.5, 10.0);
        timestamp = LocalDateTime.of(2024, 6, 1, 10, 0);
    }

    // 1. Valor físico inválido, sin persistencia
    @Test
    void dadoValorFueraDeRangoFisico_retornaError() {
        OperationResult<Medicion> resultado = useCase.ejecutar(-1.0, "pH", "Estación-1", timestamp);

        assertFalse(resultado.isExitoso());
        assertTrue(resultado.getMensaje().contains("fuera del rango físico"));
        verifyNoInteractions(medicionRepository, umbralRepository,
                alertaRepository, alarmaRepository, notificacion);
    }
    // 2. Sin umbral configurado, sin persistencia de medición
    @Test
    void sinUmbralConfigurado_retornaError() {
        when(umbralRepository.obtenerPorVariable("pH")).thenReturn(Optional.empty());

        OperationResult<Medicion> resultado = useCase.ejecutar(7.0, "pH", "Estación-1", timestamp);

        assertFalse(resultado.isExitoso());
        assertTrue(resultado.getMensaje().contains("No hay umbrales configurados"));
        verifyNoInteractions(medicionRepository, alertaRepository,
                alarmaRepository, notificacion);
    }
    // 3. Valor NORMAL, medición guardada, sin alerta ni notificación
    @Test
    void dadoValorNormal_guardaMedicionSinAlerta() {
        when(umbralRepository.obtenerPorVariable("pH")).thenReturn(Optional.of(umbralPH));

        Medicion medicionGuardada = new Medicion(
                1L, Variable.pH(), 7.0, timestamp, EstadoCriticidad.NORMAL, "Estación-1");
        when(medicionRepository.guardar(any())).thenReturn(medicionGuardada);

        OperationResult<Medicion> resultado = useCase.ejecutar(7.0, "pH", "Estación-1", timestamp);

        assertTrue(resultado.isExitoso());
        assertEquals(medicionGuardada, resultado.getDatos());
        verify(medicionRepository).guardar(any());
        verifyNoInteractions(alertaRepository, alarmaRepository, notificacion);
    }

    // 4. Valor ADVERTENCIA, alerta guardada, notifica solo a ADVERTENCIA_Y_CRITICO
    @Test
    void dadoValorAdvertencia_guardaAlertaYNotificaSoloAQuienCorresponde() {
        when(umbralRepository.obtenerPorVariable("pH")).thenReturn(Optional.of(umbralPH));

        Medicion medicionGuardada = new Medicion(
                2L, Variable.pH(), 5.5, timestamp, EstadoCriticidad.ADVERTENCIA, "Estación-1");
        when(medicionRepository.guardar(any())).thenReturn(medicionGuardada);

        ConfiguracionAlarma configAdvertencia = new ConfiguracionAlarma(
                "ops@laventurosa.com",
                ConfiguracionAlarma.NivelNotificacion.ADVERTENCIA_Y_CRITICO);
        ConfiguracionAlarma configSoloCritico = new ConfiguracionAlarma(
                "mgr@laventurosa.com",
                ConfiguracionAlarma.NivelNotificacion.SOLO_CRITICO);
        when(alarmaRepository.listarTodas()).thenReturn(List.of(configAdvertencia, configSoloCritico));

        OperationResult<Medicion> resultado = useCase.ejecutar(
                5.5, "pH", "Estación-1", timestamp);

        assertTrue(resultado.isExitoso());
        verify(alertaRepository).guardar(any());
        verify(notificacion, times(1))
                .enviar(eq("ops@laventurosa.com"), anyString(), anyString());
        verify(notificacion, never())
                .enviar(eq("mgr@laventurosa.com"), anyString(), anyString());
    }

    // 5. Valor CRÍTICO, notifica a todos los destinatarios activos
    @Test
    void dadoValorCritico_notificaATodosLosDestinatarios() {
        when(umbralRepository.obtenerPorVariable("pH")).thenReturn(Optional.of(umbralPH));

        Medicion medicionGuardada = new Medicion(
                3L, Variable.pH(), 3.5, timestamp, EstadoCriticidad.CRITICO, "Estación-1");
        when(medicionRepository.guardar(any())).thenReturn(medicionGuardada);

        ConfiguracionAlarma config1 = new ConfiguracionAlarma(
                "ops@laventurosa.com",
                ConfiguracionAlarma.NivelNotificacion.ADVERTENCIA_Y_CRITICO);
        ConfiguracionAlarma config2 = new ConfiguracionAlarma(
                "mgr@laventurosa.com",
                ConfiguracionAlarma.NivelNotificacion.SOLO_CRITICO);
        when(alarmaRepository.listarTodas()).thenReturn(List.of(config1, config2));

        OperationResult<Medicion> resultado = useCase.ejecutar(
                3.5, "pH", "Estación-1", timestamp);

        assertTrue(resultado.isExitoso());
        verify(alertaRepository).guardar(any());
        verify(notificacion, times(2)).enviar(anyString(), anyString(), anyString());
    }

    // 6. Fallo en NotificacionService, excepción atrapada, resultado sigue ok (eso causa el error)
    @Test
    void errorEnNotificacion_noPropagaExcepcion() {
        when(umbralRepository.obtenerPorVariable("pH")).thenReturn(Optional.of(umbralPH));

        Medicion medicionGuardada = new Medicion(
                4L, Variable.pH(), 3.5, timestamp, EstadoCriticidad.CRITICO, "Estación-1");
        when(medicionRepository.guardar(any())).thenReturn(medicionGuardada);

        ConfiguracionAlarma config = new ConfiguracionAlarma(
                "ops@laventurosa.com",
                ConfiguracionAlarma.NivelNotificacion.ADVERTENCIA_Y_CRITICO);
        when(alarmaRepository.listarTodas()).thenReturn(List.of(config));
        doThrow(new RuntimeException("SMTP caído"))
                .when(notificacion).enviar(anyString(), anyString(), anyString());

        OperationResult<Medicion> resultado = useCase.ejecutar(
                3.5, "pH", "Estación-1", timestamp);

        assertTrue(resultado.isExitoso());
        assertEquals(medicionGuardada, resultado.getDatos());
    }

    // 7. Sin timestamp, se asigna fecha/hora automática (no null)
    @Test
    void sinTimestamp_asignaFechaHoraAutomatica() {
        when(umbralRepository.obtenerPorVariable("pH")).thenReturn(Optional.of(umbralPH));

        when(medicionRepository.guardar(any())).thenAnswer(inv -> {
            Medicion m = inv.getArgument(0);
            assertNotNull(m.getFechaHora(), "La fechaHora no debe ser null");
            return new Medicion(5L, m.getVariable(), m.getValor(),
                    m.getFechaHora(), m.getEstado(), m.getPuntoMonitoreo());
        });

        OperationResult<Medicion> resultado = useCase.ejecutar(
                7.0, "pH", "Estación-1", null);

        assertTrue(resultado.isExitoso());
    }

    // 8. Sin configuraciones de alarma,no se llama a notificacion.enviar
    @Test
    void sinConfiguracionesDeAlarma_noSeEnvianNotificaciones() {
        when(umbralRepository.obtenerPorVariable("pH")).thenReturn(Optional.of(umbralPH));

        Medicion medicionGuardada = new Medicion(
                6L, Variable.pH(), 3.5, timestamp, EstadoCriticidad.CRITICO, "Estación-1");
        when(medicionRepository.guardar(any())).thenReturn(medicionGuardada);
        when(alarmaRepository.listarTodas()).thenReturn(Collections.emptyList());

        OperationResult<Medicion> resultado = useCase.ejecutar(
                3.5, "pH", "Estación-1", timestamp);

        assertTrue(resultado.isExitoso());
        verifyNoInteractions(notificacion);
    }
}