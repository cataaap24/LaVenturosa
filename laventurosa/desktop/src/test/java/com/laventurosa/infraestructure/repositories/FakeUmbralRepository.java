package com.laventurosa.infraestructure.repositories;

import com.laventurosa.entities.Umbral;
import com.laventurosa.usecases.ports.UmbralRepository;

import java.util.*;

public class FakeUmbralRepository implements UmbralRepository {
    private final List<Umbral> listaUmbrales = new ArrayList<>();
    private long idActual = 1;

    @Override
    public Umbral guardar(Umbral umbral) {
        Optional<Umbral> existenteOpt = obtenerPorPuntoYVariable(umbral.getPuntoMonitoreo(), umbral.getVariable().getNombre());
        long idParaAsignar = idActual;


        if (existenteOpt.isPresent()) {
            Umbral existente = existenteOpt.get();
            idParaAsignar = existente.getId();
            listaUmbrales.remove(existente);
        } else {
            idActual++;
        }

        Umbral umbralGuardado = new Umbral(
                idParaAsignar,
                umbral.getVariable(),
                umbral.getPuntoMonitoreo(),
                umbral.getMinCritico(),
                umbral.getMinAdvertencia(),
                umbral.getMaxAdvertencia(),
                umbral.getMaxCritico()
        );

        listaUmbrales.add(umbralGuardado);
        return umbralGuardado;
    }

    @Override
    public Optional<Umbral> obtenerPorPuntoYVariable(String puntoMonitoreo, String nombreVariable) {

        for (Umbral u : listaUmbrales) {
            if (u.getPuntoMonitoreo().equalsIgnoreCase(puntoMonitoreo) &&
                    u.getVariable().getNombre().equalsIgnoreCase(nombreVariable)) {

                return Optional.of(u);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Umbral> listarTodos() {
        return new ArrayList<>(listaUmbrales);
    }

    @Override
    public List<Umbral> listarPorPunto(String puntoMonitoreo) {
        List<Umbral> filtrados = new ArrayList<>();

        for (Umbral u : listaUmbrales) {
            if (u.getPuntoMonitoreo().equalsIgnoreCase(puntoMonitoreo)) {
                filtrados.add(u);
            }
        }
        return filtrados;
    }
}
