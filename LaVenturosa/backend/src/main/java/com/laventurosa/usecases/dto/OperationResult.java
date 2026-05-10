package com.laventurosa.usecases.dto;

public class OperationResult<T> {
    private final boolean exitoso;
    private final String  mensaje;
    private final T datos;

    private OperationResult(boolean exitoso, String mensaje, T datos) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public static <T> OperationResult<T> ok(String mensaje, T datos) {
        return new OperationResult<>(true, mensaje, datos);
    }
    public static <T> OperationResult<T> ok(String mensaje) {
        return new OperationResult<>(true, mensaje, null);
    }
    public static <T> OperationResult<T> error(String mensaje) {
        return new OperationResult<>(false, mensaje, null);
    }

    public boolean isExitoso() { return exitoso; }
    public String  getMensaje(){ return mensaje; }
    public T getDatos()  { return datos; }
}

