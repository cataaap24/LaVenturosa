package com.laventurosa.usecases.dto;

public class UmbralDTO {
    private Long id;
    private String variableNombre;
    private String puntoMonitoreo;
    private double minCritico;
    private double minAdvertencia;
    private double maxAdvertencia;
    private double maxCritico;


    public UmbralDTO() {
    }

    public UmbralDTO(Long id, String variableNombre, String puntoMonitoreo, double minCritico, double minAdvertencia, double maxAdvertencia, double maxCritico) {
        this.id = id;
        this.variableNombre = variableNombre;
        this.puntoMonitoreo = puntoMonitoreo;
        this.minCritico = minCritico;
        this.minAdvertencia = minAdvertencia;
        this.maxAdvertencia = maxAdvertencia;
        this.maxCritico = maxCritico;
    }

    // Getters
    public Long getId() { return id; }
    public String getVariableNombre() { return variableNombre; }
    public String getPuntoMonitoreo() { return puntoMonitoreo; }
    public double getMinCritico() { return minCritico; }
    public double getMinAdvertencia() { return minAdvertencia; }
    public double getMaxAdvertencia() { return maxAdvertencia; }
    public double getMaxCritico() { return maxCritico; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setVariableNombre(String variableNombre) { this.variableNombre = variableNombre; }
    public void setPuntoMonitoreo(String puntoMonitoreo) { this.puntoMonitoreo = puntoMonitoreo; }
    public void setMinCritico(double minCritico) { this.minCritico = minCritico; }
    public void setMinAdvertencia(double minAdvertencia) { this.minAdvertencia = minAdvertencia; }
    public void setMaxAdvertencia(double maxAdvertencia) { this.maxAdvertencia = maxAdvertencia; }
    public void setMaxCritico(double maxCritico) { this.maxCritico = maxCritico; }
}
