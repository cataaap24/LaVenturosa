package com.laventurosa.adapters.rest;

import com.laventurosa.entities.Medicion;
import com.laventurosa.usecases.dto.MedicionDTO;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.usecases.services.RegistrarMedicionUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Endpoints:
 *   POST /api/mediciones/batch - ESP32 envía batch de 3 sensores
 *   POST /api/mediciones - envío individual
 *   GET /api/mediciones/ultimas - desktop consulta última medición de cada punto
 *   GET /api/mediciones - desktop consulta historial por rango
 *   GET /api/mediciones/health - ping
 */

/**
 * @RestController: Define que esta clase es un controlador donde cada método 
 * devuelve un objeto (JSON) en lugar de una vista HTML
 * 
 * @RequestMapping: Define la ruta base para todos los endpoints de esta clase
 */

@RestController
@RequestMapping("/api/mediciones")
public class MedicionRestController {

    private final RegistrarMedicionUseCase registrarMedicion;
    private final MedicionRepository medicionRepository;

    public MedicionRestController(RegistrarMedicionUseCase registrarMedicion, MedicionRepository medicionRepository) {
        this.registrarMedicion = registrarMedicion;
        this.medicionRepository = medicionRepository;
    }
/**
     * @PostMapping: Maneja peticiones HTTP POST (crear datos)
     * @RequestBody: Indica a Spring que tome el cuerpo del JSON recibido y lo convierta en una lista de objetos porque el esp32 envía 3 a la vez
     */
    @PostMapping("/batch")
    public ResponseEntity<String> batch(@RequestBody List<MedicionRequest> body) {
        List<String> errores = new ArrayList<>();
        int ok = 0;
        for (MedicionRequest req : body) {
            OperationResult<Medicion> r = registrarMedicion.ejecutar(
                req.valor(),
                req.variable() != null ? req.variable() : "pH",
                req.puntoMonitoreo() != null ? req.puntoMonitoreo() : "Laguna-Punto-1",
                null);
            if (r.isExitoso()) ok++;
            else errores.add(req.puntoMonitoreo() + ": " + r.getMensaje());
        }
        if (errores.isEmpty())
            return ResponseEntity.ok(ok + " mediciones registradas.");
        return ResponseEntity.badRequest() // Retorna error 400 si algo falló
            .body(ok + " OK. Errores: " + String.join(", ", errores));
    }

// Endpoint envíos individuales
    @PostMapping
    public ResponseEntity<String> individual(@RequestBody MedicionRequest req) {
        OperationResult<Medicion> r = registrarMedicion.ejecutar(
            req.valor(),
            req.variable() != null ? req.variable() : "pH",
            req.puntoMonitoreo() != null ? req.puntoMonitoreo() : "Laguna-Punto-1",
            null);
        // Operador ternario para decidir el código de respuesta HTTP (200 o 400)
        return r.isExitoso() ? ResponseEntity.ok(r.getMensaje()) : ResponseEntity.badRequest().body(r.getMensaje());
    }

/**
     * @GetMapping: Maneja peticiones HTTP GET (consultar datos)
     * Recupera el estado más reciente de los puntos críticos de la laguna
     */
    @GetMapping("/ultimas")
    public ResponseEntity<List<MedicionDTO>> ultimas() {
        List<String> puntos = List.of(
            "Laguna-Entrada", "Laguna-Produccion", "Laguna-Canio");
        List<MedicionDTO> resultado = new ArrayList<>();
        for (String punto : puntos) {
            medicionRepository.obtenerUltimaPorPunto(punto).map(this::toDTO).ifPresent(resultado::add); // Convierte entidad a DTO para no exponer la base de datos directamente
        }
        return ResponseEntity.ok(resultado);
    }

/**
     * Consulta histórica con filtros.
     * @RequestParam: Extrae los parámetros de la URL (ej: ?desde=2024-05-01T00:00:00)
     * @DateTimeFormat: Asegura que el texto de la URL se convierta correctamente a un objeto de fecha Java.
     */
    @GetMapping
    public ResponseEntity<List<MedicionDTO>> historial(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {

        // Lógica por defecto si vienen nulos
        if (hasta == null) hasta = LocalDateTime.now();
        if (desde == null) desde = hasta.minusDays(1); // Último día por defecto

        List<Medicion> mediciones = medicionRepository.obtenerPorRango(desde, hasta);
        List<MedicionDTO> dtos = mediciones.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

/**
     * Endpoint de monitoreo del sistema (Health Check).
     * Sirve para que Render sepa que la aplicación no se ha caído.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Backend La Venturosa activo ✓");
    }

/**
     * Método privado de utilidad para transformar datos. 
     * Cumple con la regla de Clean Architecture de no enviar Entidades de base de datos a la UI.
     */
    private MedicionDTO toDTO(Medicion m) {
        return new MedicionDTO(
            m.getId(),
            m.getVariable().getNombre(),
            m.getVariable().getUnidad(),
            m.getValor(),
            m.getFechaHora(),
            m.getEstado(),
            m.getPuntoMonitoreo()
        );
    }
/**
     * Record: Una forma moderna y compacta (Java 14+) de crear clases de datos inmutables.
     * Representa la estructura del JSON que el ESP32 debe enviar.
     */
    public record MedicionRequest(double valor, String variable, String puntoMonitoreo) {}
    // Endpoint de prueba — no toca BD, solo confirma que llegó el dato

    //prueba
@PostMapping("/test")
public ResponseEntity<String> test(@RequestBody String body) {
    System.out.println("[TEST] Recibido desde ESP32: " + body);
    return ResponseEntity.ok("✓ Datos recibidos correctamente: " + body);
}
}

