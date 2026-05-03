package com.laventurosa.entities;

public class Umbral {
    private String id;
    private Variable variable;
    private String puntoMonitoreo;   
    private double minCritico;
    private double minAdvertencia;
    private double maxAdvertencia;
    private double maxCritico;

    public Umbral(String id, Variable variable, String puntoMonitoreo, double minCritico, double minAdvertencia, double maxAdvertencia, double maxCritico) {
        validar(variable, minCritico, minAdvertencia, maxAdvertencia, maxCritico);
        this.id = id;
        this.variable = variable;
        this.puntoMonitoreo = puntoMonitoreo;
        this.minCritico = minCritico;
        this.minAdvertencia = minAdvertencia;
        this.maxAdvertencia = maxAdvertencia;
        this.maxCritico = maxCritico;
    }

    public Umbral(Variable variable, String puntoMonitoreo, double minCritico, double minAdvertencia, double maxAdvertencia, double maxCritico) {
        this(null, variable, puntoMonitoreo, minCritico, minAdvertencia, maxAdvertencia, maxCritico);
    }

    private static void validar(Variable v, double minC, double minA, double maxA, double maxC) {
        if (!v.esValorFisicoValido(minC) || !v.esValorFisicoValido(maxC)) {
            throw new IllegalArgumentException("Umbrales críticos fuera del rango físico de " + v.getNombre());
        }
        if (!(minC < minA && minA < maxA && maxA < maxC)) {
            throw new IllegalArgumentException("Deben cumplirse: minCrítico < minAdvertencia < maxAdvertencia < maxCrítico. " + "Recibidos: " + minC + " / " + minA + " / " + maxA + " / " + maxC);
        }
    }

    public EstadoCriticidad evaluarEstado(double valor) {
        if (valor >= minAdvertencia && valor <= maxAdvertencia) {
          return EstadoCriticidad.NORMAL;
        }
        if (valor >= minCritico && valor <= maxCritico) {
          return EstadoCriticidad.ADVERTENCIA;
        }
        return EstadoCriticidad.CRITICO;
    }

    public String getId() { return id; }
    public Variable getVariable() { return variable; }
    public String getPuntoMonitoreo() { return puntoMonitoreo; }
    public double getMinCritico() { return minCritico; }
    public double getMinAdvertencia() { return minAdvertencia; }
    public double getMaxAdvertencia() { return maxAdvertencia; }
    public double getMaxCritico() { return maxCritico; }
}
