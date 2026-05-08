package com.laventurosa.infrastructure.services;

import com.laventurosa.usecases.ports.NotificacionService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
            // Cuerpo del JSON para Resend
            String json = """
                {
                    "from": "Sistema La Venturosa <onboarding@resend.dev>",
                    "to": ["%s"],
                    "subject": "%s",
                    "html": "<html><body>%s</body></html>"
                }
                """.formatted(emailDestinatario, asunto, mensaje);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        System.out.println("[Resend] Status: " + response.statusCode());
                        System.out.println("[Resend] Body: " + response.body());
                    });

        } catch (Exception e) {
            System.err.println("[Resend Error] " + e.getMessage());
        }
    }
}