package com.laventurosa.entities;

public enum EstadoCriticidad {
    NORMAL, ADVERTENCIA, CRITICO;

    public String getEtiqueta() {
        switch (this) {
            case NORMAL:
                return "Normal";
            case ADVERTENCIA:
                return "Advertencia";
            case CRITICO:
                return "Crítico";
            default:
                return "Desconocido";
        }
    }
}
