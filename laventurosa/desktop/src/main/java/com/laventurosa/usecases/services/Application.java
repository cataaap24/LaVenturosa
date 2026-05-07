package com.laventurosa.usecases.services;
import com.laventurosa.usecases.ports.MedicionRepository;
import com.laventurosa.infrastructure.repositories.PostgresMedicionRepository;
import com.laventurosa.usecases.dto.OperationResult;

public class Application {
    private VisualizarEstadoLagunaUseCase visualizarEstadoLagunaUseCase;
    private MedicionRepository medicionRepository;

    public Application() {
        this.medicionRepository = new PostgresMedicionRepository();
        this.visualizarEstadoLagunaUseCase = new VisualizarEstadoLagunaUseCase(medicionRepository);
    }

    public Application(MedicionRepository medicionRepository) {
        this.medicionRepository = medicionRepository;
    }

    public OperationResult guardarMedicion() {
        return visualizarEstadoLagunaUseCase.execute();
    }
}
