package com.laventurosa.usecases.services;

import com.laventurosa.entities.*;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.*;

import java.time.LocalDateTime;
import java.util.List;

public class RegistrarMedicionUseCase {

    private final MedicionRepository medicionRepository;
    private final UmbralRepository umbralRepository;
    private final AlertaRepository alertaRepository;
    private final ConfiguracionAlarmaRepository alarmaRepository;
    private final NotificacionService notificacion;

    public RegistrarMedicionUseCase(MedicionRepository medicionRepository, UmbralRepository umbralRepository, AlertaRepository alertaRepository, ConfiguracionAlarmaRepository alarmaRepository,
                                     NotificacionService notificacion) {
        this.medicionRepository = medicionRepository;
        this.umbralRepository = umbralRepository;
        this.alertaRepository = alertaRepository;
        this.alarmaRepository = alarmaRepository;
        this.notificacion = notificacion;
    }

    public OperationResult<Medicion> ejecutar(double valor, String nombreVariable,String puntoMonitoreo, LocalDateTime timestamp) {
        Variable variable = resolverVariable(nombreVariable);

        if (!variable.esValorFisicoValido(valor)) {
            return OperationResult.error(
                "Valor " + valor + " fuera del rango físico de " + variable.getNombre()
            );
        }
        LocalDateTime fechaHora;

        if (timestamp != null) {
            // fecha del sensor
            fechaHora = timestamp;
        } else {
            // Si no, fecha server
            fechaHora = LocalDateTime.now();
        }
        
        Optional<Umbral> umbralOptional = umbralRepository.obtenerPorVariable(variable.getNombre());

        // isPresent() para verificar si existe
        if (umbralOptional.isPresent()) {
            Umbral umbral = umbralOptional.get();
        } else {
            return OperationResult.error(
                "No hay umbrales configurados para " + variable.getNombre()
            );
        }
 
        EstadoCriticidad estado = umbral.evaluarEstado(valor);
        Medicion medicion = new Medicion(variable, valor, fechaHora, estado, puntoMonitoreo);
        Medicion guardada = medicionRepository.guardar(medicion);
        if (estado != EstadoCriticidad.NORMAL) {
            Alerta alerta = new Alerta(variable, estado, valor, umbral.getMinAdvertencia(), umbral.getMaxAdvertencia(), puntoMonitoreo);
            alertaRepository.guardar(alerta);

            String mensaje = alerta.generarMensaje();
            String asunto = "[La Venturosa] Alerta " + estado.getEtiqueta() + " - " + variable.getNombre();

            List<ConfiguracionAlarma> configs = alarmaRepository.listarTodas();
            for (ConfiguracionAlarma config : configs) {
                if (config.debeNotificar(estado)) {
                    try {
                        notificacion.enviar(config.getEmailDestinatario(), asunto, mensaje);
                    } catch (Exception e) {
                        System.err.println("Error enviando a "+ config.getEmailDestinatario() + ": " + e.getMessage());
                    }
                }
            }
        }

        return OperationResult.ok("Medición registrada correctamente.", guardada);
    }

    private Variable resolverVariable(String nombre) {
        // Manejo de nulos para evitar errores de ejecución
        if (nombre == null) {
            return Variable.pH();
        }

        switch (nombre.toLowerCase()) {
            case "ph":
                return Variable.pH();
            
            case "oxigenodisuelto":
                return Variable.oxigenoDisuelto();
            
            case "temperatura":
                return Variable.temperatura();
            
            default:
                return Variable.pH();
        }
    }
}

