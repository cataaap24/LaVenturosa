package com.laventurosa.usecases.services;

import com.laventurosa.entities.Umbral;
import com.laventurosa.infraestructure.repositories.FakeUmbralRepository;
import com.laventurosa.usecases.dto.OperationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurarUmbralesUseCaseTest {
        private ConfigurarUmbralesUseCase configurarUmbralesUseCase;
        private FakeUmbralRepository fakeUmbralRepository;

        @BeforeEach
        public void setUp() {
            fakeUmbralRepository = new FakeUmbralRepository();
            configurarUmbralesUseCase = new ConfigurarUmbralesUseCase(fakeUmbralRepository);
        }

        @Test
        public void execute_ParametrosCumplenLasReglas() {
            // Precondiciones
            String punto = "GLOBAL";
            String nombreVar = "pH";
            double minC = 6.0;
            double minA = 6.5;
            double maxA = 8.5;
            double maxC = 9.0;

            //Ejecucion
            OperationResult resultado = configurarUmbralesUseCase.execute(punto, nombreVar, minC, minA, maxA, maxC);

            //Asserts

            assertTrue(resultado.isSuccess(), "El resultado operativo debería ser exitoso.");
            assertEquals("Umbrales actualizados con éxito.", resultado.getMessage());

            // Verificación de persistencia en el repositorio modular de pruebas
            var umbralGuardadoOpt = fakeUmbralRepository.obtenerPorPuntoYVariable(punto, nombreVar);
            assertTrue(umbralGuardadoOpt.isPresent(), "El umbral debió guardarse en el repositorio.");

            Umbral umbral = umbralGuardadoOpt.get();
            assertNotNull(umbral.getId(), "El repositorio debió asignarle un ID autoincremental ficticio.");
            assertEquals(6.0, umbral.getMinCritico());
            assertEquals(9.0, umbral.getMaxCritico());
        }

        @Test
        public void execute_LosUmbralesSonIncoherentes() {
            // Escenario donde se rompe la regla minCrítico < minAdvertencia
            String punto = "GLOBAL";
            String nombreVar = "pH";
            double minC = 7.0;
            double minA = 6.5;
            double maxA = 8.5;
            double maxC = 9.0;

            OperationResult resultado = configurarUmbralesUseCase.execute(punto, nombreVar, minC, minA, maxA, maxC);

            assertFalse(resultado.isSuccess(), "El resultado operativo debería indicar un fallo.");
            assertTrue(resultado.getMessage().contains("Datos de configuración inválidos"),
                    "El mensaje debería explicar que los datos son inválidos.");
        }

    @Test
    public void execute_LaVariableNoExiste() {
        String punto = "GLOBAL";
        String nombreVar = "VariableInexistenteQueNoSoportaElSistema"; 

        OperationResult resultado = configurarUmbralesUseCase.execute(punto, nombreVar, 6.0, 6.5, 8.5, 9.0);

        assertFalse(resultado.isSuccess());
        assertTrue(resultado.getMessage().contains("Datos de configuración inválidos"));
    }
    }



