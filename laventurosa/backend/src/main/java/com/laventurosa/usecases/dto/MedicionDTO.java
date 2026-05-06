package com.laventurosa.usecases.dto;

import com.laventurosa.entities.EstadoCriticidad;
import java.time.LocalDateTime;

public class MedicionDTO {
    public final Long id;
    public final String variableNombre;
    public final String variableUnidad;
    public final double valor;
    public final LocalDateTime fechaHora;
    public final EstadoCriticidad estado;
    public final String puntoMonitoreo;

    public MedicionDTO(Long id, String variableNombre, String variableUnidad, double valor, LocalDateTime fechaHora, EstadoCriticidad estado, String puntoMonitoreo) {
        this.id = id;
        this.variableNombre = variableNombre;
        this.variableUnidad = variableUnidad;
        this.valor = valor;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.puntoMonitoreo = puntoMonitoreo;
    }
}
