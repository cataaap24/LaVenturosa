package com.laventurosa.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariableTest {

    private Variable variablePH;

    @BeforeEach
    void setUp() {
        variablePH = Variable.fromNombre("pH");
    }

    @Test
    void fromNombre() {
        assertNotNull(variablePH);
        assertEquals("pH", variablePH.getNombre());
        assertEquals("pH", variablePH.getUnidad());
        assertEquals(0.0, variablePH.getRangoFisicoMin());
        assertEquals(14.0, variablePH.getRangoFisicoMax());

        // Caso de falla
        assertThrows(IllegalArgumentException.class, () -> {
            Variable.fromNombre("VariableInexistente");
        });
    }

    @Test
    void esValorFisicoValido() {
        // Valores válidos
        assertTrue(variablePH.esValorFisicoValido(7.0));
        assertTrue(variablePH.esValorFisicoValido(0.0));
        assertTrue(variablePH.esValorFisicoValido(14.0));
        // Valores inválidos
        assertFalse(variablePH.esValorFisicoValido(-0.5));
        assertFalse(variablePH.esValorFisicoValido(14.1));
    }

    @Test
    void getNombre() {
        assertEquals("pH", variablePH.getNombre());
    }

    @Test
    void getUnidad() {
        assertEquals("pH", variablePH.getUnidad());
    }

    @Test
    void getRangoFisicoMin() {
        assertEquals(0.0, variablePH.getRangoFisicoMin());
    }

    @Test
    void getRangoFisicoMax() {
        assertEquals(14.0, variablePH.getRangoFisicoMax());
    }
}
