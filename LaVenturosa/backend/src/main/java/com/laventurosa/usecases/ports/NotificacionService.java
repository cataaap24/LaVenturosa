package com.laventurosa.usecases.ports;

public interface NotificacionService {
    void enviar(String emailDestinatario, String asunto, String mensaje);
}
