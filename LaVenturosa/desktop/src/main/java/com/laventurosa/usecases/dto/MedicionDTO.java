package com.laventurosa.usecases.dto;

import com.laventurosa.entities.EstadoCriticidad;
import java.time.OffsetDateTime;

public class MedicionDTO {
    private Long id;
    private String variable;
    private double valor;
    private OffsetDateTime fechaHora;
    private EstadoCriticidad estado;
    private String puntoMonitoreo;

    public MedicionDTO(Long id, String variable, double valor,
                              OffsetDateTime fechaHora, EstadoCriticidad estado, String puntoMonitoreo) {
        this.id = id;
        this.variable = variable;
        this.valor = valor;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.puntoMonitoreo = puntoMonitoreo;
    }

    public Long getId() { return id; }
    public String getVariable() { return variable; }
    public double getValor() { return valor; }
    public OffsetDateTime getFechaHora() { return fechaHora; }
    public EstadoCriticidad getEstado() { return estado; }
    public String getPuntoMonitoreo() { return puntoMonitoreo; }
}
