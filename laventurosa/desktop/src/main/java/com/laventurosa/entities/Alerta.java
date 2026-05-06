package com.laventurosa.entities;

import java.time.LocalDateTime;

public class Alerta {
    private Long id;
    private Variable variable;
    private EstadoCriticidad nivel;
    private double valorRegistrado;
    private double umbralMinNormal;
    private double umbralMaxNormal;
    private String puntoMonitoreo;
    private LocalDateTime fechaHora;

    public Alerta(Long id, Variable variable, EstadoCriticidad nivel, double valorRegistrado, double umbralMinNormal, double umbralMaxNormal, String puntoMonitoreo, LocalDateTime fechaHora) {
        this.id = id;
        this.variable = variable;
        this.nivel = nivel;
        this.valorRegistrado = valorRegistrado;
        this.umbralMinNormal = umbralMinNormal;
        this.umbralMaxNormal = umbralMaxNormal;
        this.puntoMonitoreo = puntoMonitoreo;
        this.fechaHora = fechaHora;
    }

    public Alerta(Variable variable, EstadoCriticidad nivel, double valorRegistrado, double umbralMinNormal, double umbralMaxNormal, String puntoMonitoreo) {
        this(null, variable, nivel, valorRegistrado, umbralMinNormal, umbralMaxNormal, puntoMonitoreo, LocalDateTime.now());
    }

    public String generarMensaje() {
        return "[" + nivel.getEtiqueta() + "] " + variable.getNombre() + "=" + valorRegistrado + " " + variable.getUnidad() + " en '" + puntoMonitoreo + "'. " + 
               "Rango normal: " + umbralMinNormal + "–" + umbralMaxNormal;
    } 
  
    public String getId() { return id; }
    public Variable getVariable() { return variable; }
    public EstadoCriticidad getNivel() { return nivel; }
    public double getValorRegistrado() { return valorRegistrado; }
    public double getUmbralMinNormal() { return umbralMinNormal; }
    public double getUmbralMaxNormal() { return umbralMaxNormal; }
    public String getPuntoMonitoreo() { return puntoMonitoreo; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}


