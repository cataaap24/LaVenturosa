package com.laventurosa.usecases.dto;

import java.time.OffsetDateTime;

public class ReporteDTO {
    private Long id;
    private String variable;
    private double valor;
    private OffsetDateTime fechaHora;
    private String estado;
    private String puntoMonitoreo;

    public ReporteDTO(Long id, String variable, double valor,
                              OffsetDateTime fechaHora, String estado, String puntoMonitoreo) {
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
    public String getEstado() { return estado; }
    public String getPuntoMonitoreo() { return puntoMonitoreo; }
}
