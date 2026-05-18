package com.laventurosa.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UmbralTest {

    private Variable variablePH;
    private Umbral umbralConId;
    private Umbral umbralSinId;

    @BeforeEach
    void setUp() {
        variablePH = Variable.fromNombre("pH");
        umbralConId = new Umbral(1L, variablePH, "Laguna-Canio", 4.0, 5.5, 8.5, 10.0);
        umbralSinId = new Umbral(variablePH, "Laguna-Produccion", 4.0, 5.5, 8.5, 10.0);
    }

    @Test
    void evaluarEstado() {
        // [5.5-8.5] NORMAL
        // [4.0-5.5) o (8.5-10.0] ADVERTENCIA
        // < 4.0 0 > 10.0 CRITICO

        // Caso 1: Valor normal
        assertEquals(EstadoCriticidad.NORMAL, umbralConId.evaluarEstado(7.0));
        assertEquals(EstadoCriticidad.NORMAL, umbralConId.evaluarEstado(5.5));
        assertEquals(EstadoCriticidad.NORMAL, umbralConId.evaluarEstado(8.5));
        // Caso 2: Valor advertencia
        assertEquals(EstadoCriticidad.ADVERTENCIA, umbralConId.evaluarEstado(5.0));
        assertEquals(EstadoCriticidad.ADVERTENCIA, umbralConId.evaluarEstado(4.0));
        assertEquals(EstadoCriticidad.ADVERTENCIA, umbralConId.evaluarEstado(9.0));
        assertEquals(EstadoCriticidad.ADVERTENCIA, umbralConId.evaluarEstado(10.0));
        // Caso 3: Valor crítico
        assertEquals(EstadoCriticidad.CRITICO, umbralConId.evaluarEstado(3.5));
        assertEquals(EstadoCriticidad.CRITICO, umbralConId.evaluarEstado(10.5));
        assertEquals(EstadoCriticidad.CRITICO, umbralConId.evaluarEstado(0.0));
    }
    // Funcion validar con sus dos validaciones: 1) minC < minA < maxA < maxC 2) Umbrales críticos dentro del rango físico de la variable
    @Test
    void validacionDeRangosIncorrectos() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Umbral(variablePH, "Punto-Canio", 6.0, 5.0, 8.0, 9.0); // minC > minA
        });
    }

    @Test
    void validacionFueraDeRangoFisicoDeVariable() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Umbral(variablePH, "Punto-Canio", -1.0, 5.5, 8.5, 10.0);
        });
    }

    @Test
    void getId() {
        assertEquals(1L, umbralConId.getId());
        assertNull(umbralSinId.getId());
    }

    @Test
    void getVariable() {
        assertSame(variablePH, umbralConId.getVariable());
    }

    @Test
    void getPuntoMonitoreo() {
        assertEquals("Laguna-Canio", umbralConId.getPuntoMonitoreo());
    }

    @Test
    void getMinCritico() {
        assertEquals(4.0, umbralConId.getMinCritico());
    }

    @Test
    void getMinAdvertencia() {
        assertEquals(5.5, umbralConId.getMinAdvertencia());
    }

    @Test
    void getMaxAdvertencia() {
        assertEquals(8.5, umbralConId.getMaxAdvertencia());
    }

    @Test
    void getMaxCritico() {
        assertEquals(10.0, umbralConId.getMaxCritico());
    }
}
