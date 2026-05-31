package com.laventurosa.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.laventurosa.entities.ConfiguracionAlarma;
import com.laventurosa.usecases.ports.ConfiguracionAlarmaRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ConfiguracionAlarmaCache implements ConfiguracionAlarmaRepository {

    private final ConfiguracionAlarmaRepository repository;

    private final Cache<String, ConfiguracionAlarma> cachePorEmail;

    private final Cache<String, List<ConfiguracionAlarma>> cacheListado;

    private static final String LISTA_KEY = "TODAS_LAS_CONFIGURACIONES";

    public ConfiguracionAlarmaCache(ConfiguracionAlarmaRepository repository) {
        this.repository = repository;

        this.cachePorEmail = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(500)
                .build();

        this.cacheListado = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(1) 
                .build();
    }

    @Override
    public ConfiguracionAlarma guardar(ConfiguracionAlarma config) {
        ConfiguracionAlarma guardada = repository.guardar(config);

        if (guardada != null) {
            cachePorEmail.invalidate(config.getEmailDestinatario());
            cacheListado.invalidate(LISTA_KEY);
            System.out.println("[CACHE-ALARMAS] Invalidado correo: " + config.getEmailDestinatario() + " y lista general");
        }
        return guardada;
    }

    @Override
    public ConfiguracionAlarma obtenerConfiguracionAlarma(String email) {
        return cachePorEmail.get(email, key -> {
            System.out.println("[CACHE-ALARMAS] Miss — consultando BD para correo: " + key);
            return repository.obtenerConfiguracionAlarma(key);
        });
    }

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
        cacheListado.invalidate(LISTA_KEY);
        cachePorEmail.invalidateAll();
        System.out.println("[CACHE-ALARMAS] Eliminación (ID: " + id + ") - Cachés completamente limpiadas");
    }
}
