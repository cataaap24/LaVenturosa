package com.laventurosa.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfiguracionAlarmaTest {
    private ConfiguracionAlarma alarmaConId;
    private ConfiguracionAlarma alarmaPorDefecto;

    @BeforeEach
    void setUp() {
        alarmaConId = new ConfiguracionAlarma(1L, "admin@gmail.com", ConfiguracionAlarma.NivelNotificacion.SOLO_CRITICO, true);
        alarmaPorDefecto = new ConfiguracionAlarma("operador@gmail.com", ConfiguracionAlarma.NivelNotificacion.ADVERTENCIA_Y_CRITICO);
    }

    @Test
    void debeNotificar() {
        // Caso 1: Alarma inactiva
        alarmaConId.setActivo(false);
        assertFalse(alarmaConId.debeNotificar(EstadoCriticidad.CRITICO));
        assertFalse(alarmaConId.debeNotificar(EstadoCriticidad.ADVERTENCIA));

        alarmaConId.setActivo(true);

        // Caso 2: Solo crítico
        assertTrue(alarmaConId.debeNotificar(EstadoCriticidad.CRITICO));
        assertFalse(alarmaConId.debeNotificar(EstadoCriticidad.ADVERTENCIA));
        assertFalse(alarmaConId.debeNotificar(EstadoCriticidad.NORMAL));

        // Caso 3: Crítico y advertencia
        assertTrue(alarmaPorDefecto.debeNotificar(EstadoCriticidad.CRITICO));
        assertTrue(alarmaPorDefecto.debeNotificar(EstadoCriticidad.ADVERTENCIA));
        assertFalse(alarmaPorDefecto.debeNotificar(EstadoCriticidad.NORMAL));
    }

    @Test
    void getId() {
        assertEquals(1L, alarmaConId.getId());
        assertNull(alarmaPorDefecto.getId());
    }

    @Test
    void getEmailDestinatario() {
        assertEquals("admin@gmail.com", alarmaConId.getEmailDestinatario());
        assertEquals("operador@gmail.com", alarmaPorDefecto.getEmailDestinatario());
    }

    @Test
    void getNivelNotificacion() {
        assertEquals(ConfiguracionAlarma.NivelNotificacion.SOLO_CRITICO, alarmaConId.getNivelNotificacion());
        assertEquals(ConfiguracionAlarma.NivelNotificacion.ADVERTENCIA_Y_CRITICO, alarmaPorDefecto.getNivelNotificacion());
    }

    @Test
    void isActivo() {
        assertTrue(alarmaConId.isActivo());
        assertTrue(alarmaPorDefecto.isActivo());
    }

    @Test
    void setActivo() {
        alarmaConId.setActivo(false);
        assertFalse(alarmaConId.isActivo());

        alarmaConId.setActivo(true);
        assertTrue(alarmaConId.isActivo());
    }
}
