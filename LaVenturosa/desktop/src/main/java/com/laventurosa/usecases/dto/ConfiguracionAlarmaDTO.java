package com.laventurosa.usecases.dto;

import com.laventurosa.entities.ConfiguracionAlarma;

public class ConfiguracionAlarmaDTO {
    private final String emailDestinatario;
    private final String nivelNotificacion;
    private boolean activo;

    public ConfiguracionAlarmaDTO(String email, String nivelNotificacion, boolean activo) {
        this.emailDestinatario = email;
        this.nivelNotificacion = nivelNotificacion;
        this.activo = activo;   
    }

    public ConfiguracionAlarmaDTO(ConfiguracionAlarma config) {
        this.emailDestinatario = config.getEmailDestinatario();
        this.nivelNotificacion = config.getNivelNotificacion().getEtiqueta();
        this.activo = config.isActivo();
    }

    public String getEmailDestinatario() { return this.emailDestinatario; }
    public String getNivelNotificacion() { return this.nivelNotificacion; }
    public boolean isActivo() { return this.activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
