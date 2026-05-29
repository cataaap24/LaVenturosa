package com.laventurosa.entities;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

// Single Assertion Principle
class AlertaTest {

    private Variable ph;

    @BeforeEach
    void setUp() {
        ph = Variable.pH();
    }

    // Pruebas constructor sin id
    @Test
    void constructor_sin_id_asigna_null_a_id() {
        Alerta alerta = new Alerta(ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio");
        assertNull(alerta.getId());
    }

    @Test
    void constructor_sin_id_asigna_fechaHora_automatica() {
        LocalDateTime antes = LocalDateTime.now();
        Alerta alerta = new Alerta(ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio");
        LocalDateTime despues = LocalDateTime.now();

        assertNotNull(alerta.getFechaHora());
        assertFalse(alerta.getFechaHora().isBefore(antes));
        assertFalse(alerta.getFechaHora().isAfter(despues));
    }
    // Pruebas constructor con id
    @Test
    void constructor_con_id_guarda_id_correctamente() {
        LocalDateTime fecha = LocalDateTime.of(2026, 5, 15, 6, 0, 0);
        Alerta alerta = new Alerta(4L, ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio", fecha);
        assertEquals(4L, alerta.getId());
    }

    @Test
    void constructor_con_id_guarda_fechaHora_indicada() {
        LocalDateTime fecha = LocalDateTime.of(2026, 5, 15, 6, 0, 0);
        Alerta alerta = new Alerta(1L, ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio", fecha);
        assertEquals(fecha, alerta.getFechaHora());
    }
    // Pruebas aspectos específicos de generarMensaje()
    @Test
    void generarMensaje_contiene_etiqueta_del_nivel() {
        Alerta alerta = new Alerta(ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio");
        assertTrue(alerta.generarMensaje().contains(EstadoCriticidad.CRITICO.getEtiqueta()));
    }

    @Test
    void generarMensaje_contiene_nombre_de_variable() {
        Alerta alerta = new Alerta(ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio");
        assertTrue(alerta.generarMensaje().contains("pH"));
    }

    @Test
    void generarMensaje_contiene_valor_registrado() {
        Alerta alerta = new Alerta(ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio");
        assertTrue(alerta.generarMensaje().contains("10.0"));
    }

    @Test
    void generarMensaje_contiene_punto_monitoreo() {
        Alerta alerta = new Alerta(ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio");
        assertTrue(alerta.generarMensaje().contains("Laguna-Canio"));
    }

    @Test
    void generarMensaje_contiene_rango_normal() {
        Alerta alerta = new Alerta(ph, EstadoCriticidad.ADVERTENCIA, 5.8, 6.5, 8.5, "Laguna-Entrada");
        String mensaje = alerta.generarMensaje();
        assertTrue(mensaje.contains("6.5"));
        assertTrue(mensaje.contains("8.5"));
    }

    @Test
    void generarMensaje_contiene_unidad_de_variable() {
        Alerta alerta = new Alerta(ph, EstadoCriticidad.ADVERTENCIA, 5.8, 6.5, 8.5, "Laguna-Entrada");
        assertTrue(alerta.generarMensaje().contains(ph.getUnidad()));
    }

    @Test
    void generarMensaje_formato_completo_para_alerta_critica() {
        Alerta alerta = new Alerta(ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio");
        String mensaje = alerta.generarMensaje();

        // Verificar que el mensaje tiene todos los componentes clave
        assertAll(
                () -> assertTrue(mensaje.contains(EstadoCriticidad.CRITICO.getEtiqueta())),
                () -> assertTrue(mensaje.contains("pH")),
                () -> assertTrue(mensaje.contains("10.0")),
                () -> assertTrue(mensaje.contains("Laguna-Canio")),
                () -> assertTrue(mensaje.contains("6.5")),
                () -> assertTrue(mensaje.contains("8.5"))
        );
    }

    // Pruebas getters
    @Test
    void getId() {
        Alerta alerta = new Alerta(42L, ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio", LocalDateTime.now());
        assertEquals(42L, alerta.getId());
    }

    @Test
    void getVariable() {
        Alerta alerta = new Alerta(42L, ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio", LocalDateTime.now());
        assertEquals(ph, alerta.getVariable());
    }

    @Test
    void getNivel() {
        Alerta alerta = new Alerta(42L, ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio", LocalDateTime.now());
        assertEquals(EstadoCriticidad.CRITICO, alerta.getNivel());
    }

    @Test
    void getValorRegistrado() {
        Alerta alerta = new Alerta(42L, ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio", LocalDateTime.now());
        assertEquals(10.0, alerta.getValorRegistrado());
    }

    @Test
    void getUmbralMinNormal() {
        Alerta alerta = new Alerta(42L, ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio", LocalDateTime.now());
        assertEquals(6.5, alerta.getUmbralMinNormal());
    }

    @Test
    void getUmbralMaxNormal() {
        Alerta alerta = new Alerta(42L, ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio", LocalDateTime.now());
        assertEquals(8.5, alerta.getUmbralMaxNormal());
    }

    @Test
    void getPuntoMonitoreo() {
        Alerta alerta = new Alerta(42L, ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio", LocalDateTime.now());
        assertEquals("Laguna-Canio", alerta.getPuntoMonitoreo());
    }

    @Test
    void getFechaHora() {
        LocalDateTime fecha = LocalDateTime.of(2025, 9, 15, 6, 0, 0);
        Alerta alerta = new Alerta(42L, ph, EstadoCriticidad.CRITICO, 10.0, 6.5, 8.5, "Laguna-Canio", fecha);
        assertEquals(fecha, alerta.getFechaHora());
    }


}