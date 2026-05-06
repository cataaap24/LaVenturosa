package com.laventurosa.entities;

import java.time.LocalDateTime;

public class Medicion {
    private Long id;
    private Variable variable;
    private double valor;
    private LocalDateTime fechaHora;
    private EstadoCriticidad estado;
    private String puntoMonitoreo;

    public Medicion(Long id, Variable variable, double valor, LocalDateTime fechaHora, EstadoCriticidad estado, String puntoMonitoreo) {
        this.id = id;
        this.variable = variable;
        this.valor = valor;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.puntoMonitoreo = puntoMonitoreo;
    }

    public Medicion(Variable variable, double valor, LocalDateTime fechaHora, EstadoCriticidad estado, String puntoMonitoreo) {
        this(null, variable, valor, fechaHora, estado, puntoMonitoreo); // BD asigna id
    }

    public boolean esValorFisicoValido() { return variable.esValorFisicoValido(valor); }

    public Long getId() { return id; }
    public Variable getVariable() { return variable; }
    public double getValor() { return valor; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public EstadoCriticidad getEstado() { return estado; }
    public String getPuntoMonitoreo() { return puntoMonitoreo; }
}
