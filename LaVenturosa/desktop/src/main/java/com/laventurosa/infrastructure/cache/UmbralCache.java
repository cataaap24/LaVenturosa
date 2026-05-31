package com.laventurosa.infrastructure.cache;
 
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.laventurosa.entities.Umbral;
import com.laventurosa.usecases.ports.UmbralRepository;
 
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
 
public class UmbralCache implements UmbralRepository {
 
    private final UmbralRepository repository;
 
    // Caché para consultas individuales por punto + variable
    // Llave compuesta: "puntoMonitoreo::nombreVariable"
    private final Cache<String, Optional<Umbral>> cachePorPuntoYVariable;
 
    // Caché para el listado completo de todos los umbrales
    private final Cache<String, List<Umbral>> cacheListadoTotal;
 
    // Caché para listados filtrados por punto de monitoreo
    private final Cache<String, List<Umbral>> cacheListadoPorPunto;
 
    private static final String LISTA_TOTAL_KEY = "TODOS_LOS_UMBRALES";
 
    public UmbralCache(UmbralRepository repository) {
        this.repository = repository;
 
        // Consultas individuales: expira en 5 min, máximo 1000 entradas
        this.cachePorPuntoYVariable = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build();
 
        // Lista total: solo 1 espacio porque almacena toda la colección
        this.cacheListadoTotal = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(1)
                .build();
 
        // Listas por punto: una entrada por cada punto de monitoreo distinto
        this.cacheListadoPorPunto = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(200)
                .build();
    }
 
    // --- Llave compuesta para la caché individual ---
    private String buildKey(String puntoMonitoreo, String nombreVariable) {
        return puntoMonitoreo + "::" + nombreVariable;
    }
 
    @Override
    public Umbral guardar(Umbral umbral) {
        // 1. Persistimos en la base de datos
        Umbral guardado = repository.guardar(umbral);
 
        // 2. Invalidamos las cachés afectadas
        if (guardado != null) {
            String key = buildKey(umbral.getPuntoMonitoreo(), umbral.getVariable().getNombre());
 
            cachePorPuntoYVariable.invalidate(key);
            cacheListadoTotal.invalidate(LISTA_TOTAL_KEY);
            cacheListadoPorPunto.invalidate(umbral.getPuntoMonitoreo());
 
            System.out.println("[CACHE-UMBRAL] Invalidado: " + key
                    + ", lista total y lista del punto: " + umbral.getPuntoMonitoreo());
        }
        return guardado;
    }
 
    @Override
    public Optional<Umbral> obtenerPorPuntoYVariable(String puntoMonitoreo, String nombreVariable) {
        String key = buildKey(puntoMonitoreo, nombreVariable);
        return cachePorPuntoYVariable.get(key, k -> {
            System.out.println("[CACHE-UMBRAL] Miss — consultando BD para: " + k);
            return repository.obtenerPorPuntoYVariable(puntoMonitoreo, nombreVariable);
        });
    }
 
    @Override
    public List<Umbral> listarTodos() {
        return cacheListadoTotal.get(LISTA_TOTAL_KEY, key -> {
            System.out.println("[CACHE-UMBRAL] Miss — consultando BD para lista total de umbrales");
            return repository.listarTodos();
        });
    }
 
    @Override
    public List<Umbral> listarPorPunto(String puntoMonitoreo) {
        return cacheListadoPorPunto.get(puntoMonitoreo, key -> {
            System.out.println("[CACHE-UMBRAL] Miss — consultando BD para punto: " + key);
            return repository.listarPorPunto(key);
        });
    }
}