package com.laventurosa.usecases.services;

import com.laventurosa.infrastructure.repositories.PostgresUmbralRepository;
import com.laventurosa.usecases.dto.OperationResult;
import com.laventurosa.usecases.ports.UmbralRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurarUmbralesUseCaseTest {

    @Test
    public void ConfigurarUmbral() {
        ConfigurarUmbralesUseCase configurarUmbralesUseCase;
        UmbralRepository umbralRepository;
        umbralRepository = new PostgresUmbralRepository();
        configurarUmbralesUseCase = new ConfigurarUmbralesUseCase(umbralRepository);

        String punto = "General";
        String nombreVar = "pH";
        double minC = 6.0;
        double minA = 6.5;
        double maxA = 8.5;
        double maxC = 9.0;

        OperationResult resultado = configurarUmbralesUseCase.execute(punto, nombreVar, minC, minA, maxA, maxC);


        assertTrue(resultado.isSuccess(), "El resultado debería indicar éxito.");
        assertEquals("Umbrales actualizados con éxito.", resultado.getMessage());

    }
}


