package com.laventurosa.infrastructure.services;

import com.laventurosa.usecases.ports.NotificacionService;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class GmailNotificacionService implements NotificacionService {

    private static final String REMITENTE;
    private static final String APP_PASSWORD;

    static {
        String envRemitente = System.getenv("GMAIL_REMITENTE");
        if (envRemitente != null) {
            REMITENTE = envRemitente;
        } else {
            REMITENTE = "correo@gmail.com";
        }
        String envPassword = System.getenv("GMAIL_APP_PASSWORD");
        if (envPassword != null) {
            APP_PASSWORD = envPassword;
        } else {
            APP_PASSWORD = "sin_password";
        }
    }

    @Override
    public void enviar(String emailDestinatario, String asunto, String mensaje) {
        // Config protocolo SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true"); // Requiere usuario y clave
        props.put("mail.smtp.starttls.enable", "true"); // Cifrado de seguridad
        props.put("mail.smtp.host", "smtp.gmail.com"); // Servidor de Google
        props.put("mail.smtp.port", "587"); // Puerto estándar para TLS
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Confianza en el certificado

        // Crear sesión
        // Se usa un Authenticator para pasar el REMITENTE y la APP_PASSWORD de forma segura
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE, APP_PASSWORD);
            }
        });

        try {
            // Construcción del msj
            Message mail = new MimeMessage(session);
            mail.setFrom(new InternetAddress(REMITENTE, "Sistema La Venturosa"));
            mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestinatario));
            mail.setSubject(asunto);

            String html = "<html><body style='font-family:Arial,sans-serif;'>" +
                    "<h2 style='color:#152654;'>Sistema de Monitoreo - Laguna La Venturosa</h2>" +
                    "<p>" + mensaje + "</p>" +
                    "<hr/><small style='color:#888;'>Este mensaje fue generado automáticamente por la alerta de la variable anormal.</small>" +
                    "</body></html>";

            // Indica que el contenido es HTML y usa codificación UTF-8 para tildes y ñ
            mail.setContent(html, "text/html; charset=utf-8");

            Transport.send(mail);
            System.out.println("[Gmail] Email enviado a " + emailDestinatario);

        } catch (Exception e) {
            throw new RuntimeException("Error enviando email: " + e.getMessage(), e);
        }
    }
}