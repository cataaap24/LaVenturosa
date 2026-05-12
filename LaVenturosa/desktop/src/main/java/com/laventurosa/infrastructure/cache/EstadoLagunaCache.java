package com.laventurosa.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.ports.MedicionRepository;

import java.util.List;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class EstadoLagunaCache implements MedicionRepository {
    private final MedicionRepository medicionRepository;

    private final Cache<String, Optional<Medicion>> cacheUltima;

    public EstadoLagunaCache(MedicionRepository medicionRepository) {
        this.medicionRepository = medicionRepository;
        this.cacheUltima = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
    }

    @Override
    public Optional<Medicion> obtenerUltimaPorPunto(String puntoMonitoreo) {
        return cacheUltima.get(puntoMonitoreo, key -> {
            System.out.println("[CACHE] Miss — consultando BD para: " + key);
            return medicionRepository.obtenerUltimaPorPunto(key);
        });
    }

    @Override
    public Medicion guardar(Medicion medicion) {
        Medicion guardada = medicionRepository.guardar(medicion);
        if (guardada != null) {
            cacheUltima.invalidate(medicion.getPuntoMonitoreo());
            System.out.println("[CACHE] Invalidado: " + medicion.getPuntoMonitoreo());
        }
        return guardada;
    }

    @Override
    public List<Medicion> obtenerUltimoMes() {
        return medicionRepository.obtenerUltimoMes();
    }

    @Override
    public List<Medicion> obtenerPorRango(OffsetDateTime desde, OffsetDateTime hasta) {
        return medicionRepository.obtenerPorRango(desde, hasta);
    }

    @Override
    public List<Medicion> obtenerPorRangoYPunto(OffsetDateTime desde, OffsetDateTime hasta, String punto) {
        return medicionRepository.obtenerPorRangoYPunto(desde, hasta, punto);
    }

    @Override
    public List<Medicion> obtenerPorRangoYVariable(OffsetDateTime desde, OffsetDateTime hasta, String variable) {
        return medicionRepository.obtenerPorRangoYVariable(desde, hasta, variable);
    }
}
