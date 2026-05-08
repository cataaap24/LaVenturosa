package com.laventurosa.infrastructure.config;

import com.laventurosa.infrastructure.repositories.*;
import com.laventurosa.infrastructure.services.GmailNotificacionService;
import com.laventurosa.infrastructure.services.ResendNotificacionService;
import com.laventurosa.usecases.ports.*;
import com.laventurosa.usecases.services.RegistrarMedicionUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BackendAppContext {


    @Bean
    public MedicionRepository medicionRepository() {
        return new PostgresMedicionRepository();
    }

    @Bean
    public UmbralRepository umbralRepository() {
        return new PostgresUmbralRepository();
    }

    @Bean
    public AlertaRepository alertaRepository() {
        return new PostgresAlertaRepository();
    }

    @Bean
    public ConfiguracionAlarmaRepository configuracionAlarmaRepository() {
        return new PostgresConfiguracionAlarmaRepository();
    }

    @Bean
    public NotificacionService notificacionService() {
        String resendKey = System.getenv("RESEND_API_KEY");
        if (resendKey != null && !resendKey.isEmpty()) {
            System.out.println("[CONFIG] Usando ResendNotificacionService");
            return new ResendNotificacionService();
        }

        System.out.println("[CONFIG] Usando GmailNotificacionService");
        return new GmailNotificacionService();
    }
    @Bean public RegistrarMedicionUseCase registrarMedicionUseCase(MedicionRepository med, UmbralRepository umb, AlertaRepository ale, ConfiguracionAlarmaRepository conf, NotificacionService not) {
        return new RegistrarMedicionUseCase(med, umb, ale, conf, not);
    }
}
