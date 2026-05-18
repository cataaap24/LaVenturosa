package com.laventurosa.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MedicionTest {
    private Variable variablePH;
    private OffsetDateTime fechaHoraActual;
    private EstadoCriticidad estadoNormal;
    private Medicion medicionConId;
    private Medicion medicionSinId;

    @BeforeEach
    void setUp() {
        variablePH = Variable.fromNombre("pH");
        fechaHoraActual = OffsetDateTime.now();
        estadoNormal = EstadoCriticidad.NORMAL;
        medicionConId = new Medicion(100L, variablePH, 7.0, fechaHoraActual, estadoNormal, "Laguna-Canio");
        medicionSinId = new Medicion(variablePH, 12.4, fechaHoraActual, EstadoCriticidad.ADVERTENCIA, "laguna-Entrada");
    }

    @Test
    void esValorFisicoValido() {
        // Caso 1
        assertTrue(medicionConId.esValorFisicoValido());
        // Caso 2
        Medicion medicionInvalida = new Medicion(variablePH, 15.0, fechaHoraActual, estadoNormal, "Laguna-Canio");
        assertFalse(medicionInvalida.esValorFisicoValido());
        // Caso 3
        Medicion medicionInvalidaMin = new Medicion(variablePH, -1.0, fechaHoraActual, estadoNormal, "Laguna-Canio");
        assertFalse(medicionInvalidaMin.esValorFisicoValido());
    }


    @Test
    void getId() {
        assertEquals(100L, medicionConId.getId());
        assertNull(medicionSinId.getId());
    }

    @Test
    void getVariable() {
        assertSame(variablePH, medicionConId.getVariable());
        // Nombre de la variable
        assertEquals("pH", medicionSinId.getVariable().getNombre());
    }

    @Test
    void getValor() {
        assertEquals(7.0, medicionConId.getValor());
    }

    @Test
    void getFechaHora() {
        assertEquals(fechaHoraActual, medicionConId.getFechaHora());
    }

    @Test
    void getEstado() {
        assertEquals(EstadoCriticidad.NORMAL, medicionConId.getEstado());
    }

    @Test
    void getPuntoMonitoreo() {
        assertEquals("Laguna-Canio", medicionConId.getPuntoMonitoreo());
    }
}
