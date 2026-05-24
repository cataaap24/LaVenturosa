// src/com/laventurosa/usecases/dto/InformeEstadoLaguna.java
package com.laventurosa.usecases.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class EstadoLagunaDTO {
    private Double valor;
    private OffsetDateTime fechaHora;
    private String puntoMonitoreo;
    private String estado;
    private List<MedicionDTO> historialReciente;
    
    public EstadoLagunaDTO(Double valor, OffsetDateTime fechaHora, String puntoMonitoreo, String estado) {
        this.valor = valor;
        this.fechaHora = fechaHora;
        this.puntoMonitoreo = puntoMonitoreo;
        this.estado = estado;
    }
    
    public EstadoLagunaDTO(Double valor, OffsetDateTime fechaHora, String puntoMonitoreo, String estado, List<MedicionDTO> historialReciente) {
        this.valor = valor;
        this.fechaHora = fechaHora;
        this.puntoMonitoreo = puntoMonitoreo;
        this.estado = estado;
        this.historialReciente = historialReciente;

    }
    public Double getValor() { return valor; }
    public OffsetDateTime getFechaHora() { return fechaHora; }
    public String getPuntoMonitoreo() { return puntoMonitoreo; }
    public String getEstado() { return estado; }
    public List<MedicionDTO> getHistorialReciente() { return historialReciente; }
    public void setValor(Double valor) { this.valor = valor; }
    public void setFechaHora(OffsetDateTime fechaHora) { this.fechaHora = fechaHora; }
    public void setPuntoMonitoreo(String puntoMonitoreo) { this.puntoMonitoreo = puntoMonitoreo; }
    public void setEstado(String estado) { this.estado = estado; }
}
