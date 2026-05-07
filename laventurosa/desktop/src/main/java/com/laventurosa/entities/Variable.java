package com.laventurosa.entities;

public class Variable {
    private final String nombre;
    private final String unidad;
    private final double rangoFisicoMin;
    private final double rangoFisicoMax;

    public Variable(String nombre, String unidad, double rangoFisicoMin, double rangoFisicoMax) {
        if (rangoFisicoMin >= rangoFisicoMax) {
            throw new IllegalArgumentException("rangoFisicoMin debe ser menor que rangoFisicoMax");
        }
        this.nombre = nombre;
        this.unidad = unidad;
        this.rangoFisicoMin = rangoFisicoMin;
        this.rangoFisicoMax = rangoFisicoMax;
    }

    //se requiere para poder mapear correctamente en donde se necesite el objeto Variable
    public static Variable fromNombre(String nombre) {
        switch (nombre) {
            case "pH": return new Variable("pH", "pH", 0.0, 14.0);
            //case "OxigenoDisuelto": return new Variable("OxigenoDisuelto", "mg/L", 0.0, 20.0);
            //case "Temperatura": return new Variable("Temperatura", "°C", 0.0, 0.0);
            default: throw new IllegalArgumentException("Variable desconocida: " + nombre);
        }
    }

    public boolean esValorFisicoValido(double valor) {
        return valor >= rangoFisicoMin && valor <= rangoFisicoMax;
    }

    public String getNombre() { return nombre; }
    public String getUnidad() { return unidad; }
    public double getRangoFisicoMin() { return rangoFisicoMin; }
    public double getRangoFisicoMax() { return rangoFisicoMax; }

    @Override public String toString() {
        return nombre + " (" + unidad + ") [" + rangoFisicoMin + "–" + rangoFisicoMax + "]";
    }
}
