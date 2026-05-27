package com.laventurosa.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ConfiguracionAlarmaCache implements ConfiguracionAlarmaRepository {

    private final ConfiguracionAlarmaRepository repository;

    // Caché para las consultas individuales por correo
    private final Cache<String, ConfiguracionAlarma> cachePorEmail;

    // Caché para el listado general del panel
    private final Cache<String, List<ConfiguracionAlarma>> cacheListado;

    // Llave estática para guardar la lista completa
    private static final String LISTA_KEY = "TODAS_LAS_CONFIGURACIONES";

    public ConfiguracionAlarmaCache(ConfiguracionAlarmaRepository repository) {
        this.repository = repository;

        // Configuramos la caché para un solo correo (expira en 5 min)
        this.cachePorEmail = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(500)
                .build();

        // Configuramos la caché para la lista completa del panel
        this.cacheListado = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(1) // Solo necesitamos 1 espacio porque guarda toda la lista
                .build();
    }

    @Override
    public ConfiguracionAlarma guardar(ConfiguracionAlarma config) {
        // 1. Mandamos a guardar/actualizar a la base de datos (Ejecuta tu UPSERT)
        ConfiguracionAlarma guardada = repository.guardar(config);

        // 2. Si fue exitoso, invalidamos las cachés
        if (guardada != null) {
            // Invalidamos el correo específico
            cachePorEmail.invalidate(config.getEmailDestinatario());
            // Invalidamos la lista completa para que el panel se refresque
            cacheListado.invalidate(LISTA_KEY);
            System.out.println("[CACHE-ALARMAS] Invalidado correo: " + config.getEmailDestinatario() + " y lista general");
        }
        return guardada;
    }

    /*@Override
    public ConfiguracionAlarma obtenerConfiguracionAlarma(String email) {
        return cachePorEmail.get(email, key -> {
            System.out.println("[CACHE-ALARMAS] Miss — consultando BD para correo: " + key);
            return repository.obtenerConfiguracionAlarma(key);
        });
    }*/

    @Override
    public List<ConfiguracionAlarma> listarTodas() {
        return cacheListado.get(LISTA_KEY, key -> {
            System.out.println("[CACHE-ALARMAS] Miss — consultando BD para toda la lista de alarmas");
            return repository.listarTodas();
        });
    }

    @Override
    public void eliminar(Long id) {
        repository.eliminar(id);

        // Al eliminar, la lista cambia, así que debemos invalidarla
        cacheListado.invalidate(LISTA_KEY);
        // Como no tenemos el email a la mano, invalidamos todo por seguridad
        cachePorEmail.invalidateAll();

        System.out.println("[CACHE-ALARMAS] Eliminación (ID: " + id + ") - Cachés completamente limpiadas");
    }
}
