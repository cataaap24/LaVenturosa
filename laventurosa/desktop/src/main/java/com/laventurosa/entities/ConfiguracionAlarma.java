package com.laventurosa.entities;

public class ConfiguracionAlarma {

  public enum NivelNotificacion {
      SOLO_CRITICO,
      ADVERTENCIA_Y_CRITICO;
  
      public String getEtiqueta() {
          switch (this) {
              case SOLO_CRITICO:
                  return "Solo críticas";
              case ADVERTENCIA_Y_CRITICO:
                  return "Advertencias y críticas";
              default:
                  return "";
          }
      }
  }

    private Long id;
    private String emailDestinatario;
    private NivelNotificacion nivelNotificacion;
    private boolean activo;

    public ConfiguracionAlarma(Long id, String emailDestinatario, NivelNotificacion nivelNotificacion, boolean activo) {
        this.id = id;
        this.emailDestinatario = emailDestinatario;
        this.nivelNotificacion = nivelNotificacion;
        this.activo = activo;
    }

    public ConfiguracionAlarma(String emailDestinatario, NivelNotificacion nivelNotificacion) {
        this(null, emailDestinatario, nivelNotificacion, true);
    }

    public boolean debeNotificar(EstadoCriticidad estado) {
        if (!activo) {
          return false;
        }
        switch (nivelNotificacion) {
            case SOLO_CRITICO:
                return estado == EstadoCriticidad.CRITICO;
            case ADVERTENCIA_Y_CRITICO:
                return estado == EstadoCriticidad.CRITICO || estado == EstadoCriticidad.ADVERTENCIA;
            default:
                return false;
        }
    }


    public String getId() { return id; }
    public String getEmailDestinatario() { return emailDestinatario; }
    public NivelNotificacion getNivelNotificacion() { return nivelNotificacion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
