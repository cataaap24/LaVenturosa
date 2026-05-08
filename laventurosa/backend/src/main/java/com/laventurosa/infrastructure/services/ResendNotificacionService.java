package com.laventurosa.infrastructure.services;

import com.laventurosa.usecases.ports.NotificacionService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ResendNotificacionService implements NotificacionService {

    private final String apiKey;
    private final HttpClient httpClient;

    public ResendNotificacionService() {
        this.apiKey = System.getenv("RESEND_API_KEY");
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public void enviar(String emailDestinatario, String asunto, String mensaje) {
        try {
            // FORZAR HORA DE COLOMBIA AQUÍ TAMBIÉN
            String horaActual = ZonedDateTime.now(ZoneId.of("America/Bogota"))
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a"));

            String htmlContent = """
                <html>
                <body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>
                    <div style='max-width: 600px; margin: 0 auto; border: 1px solid #eee; padding: 20px; border-radius: 10px;'>
                        <h2 style='color: #152654; border-bottom: 2px solid #152654; padding-bottom: 10px;'>
                              Sistema de Monitoreo - La Venturosa
                        </h2>
                        <p style='font-size: 16px;'>Ha ocurrido un evento que requiere su atención:</p>
                        <div style='background-color: #f9f9f9; padding: 15px; border-left: 5px solid #152654; margin: 20px 0;'>
                            <strong>Detalle:</strong> %s
                        </div>
                        <p style='font-size: 14px; color: #555;'>
                              <strong>Fecha y Hora (Colombia):</strong> %s
                        </p>
                        <hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;' />
                        <p style='font-size: 12px; color: #888; text-align: center;'>
                            Este mensaje fue generado automáticamente por la plataforma de telemetría.<br>
                            © 2026 Laguna La Venturosa - Proyecto de Ingeniería
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(mensaje, horaActual);

            String json = """
                {
                    "from": "Sistema La Venturosa <onboarding@resend.dev>",
                    "to": ["%s"],
                    "subject": "%s",
                    "html": "%s"
                }
                """.formatted(
                    emailDestinatario, 
                    asunto, 
                    htmlContent.replace("\"", "\\\"").replace("\n", "")
                );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        System.out.println("[Resend] Status: " + response.statusCode());
                    });

        } catch (Exception e) {
            System.err.println("[Resend Error] " + e.getMessage());
        }
    }
}
