package com.laventurosa.usecases.dto;

public class OperationResult<T> {
    private final boolean success;
    private final String message;
    private final T data;

    private OperationResult(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Éxito con datos
    public static <T> OperationResult<T> ok(String message, T data) {
        return new OperationResult<>(true, message, data);
    }

    // Éxito sin datos (por ejemplo, para un "Guardado correctamente")
    public static <T> OperationResult<T> ok(String message) {
        return new OperationResult<>(true, message, null);
    }

    // Fallo
    public static <T> OperationResult<T> fail(String message) {
        return new OperationResult<>(false, message, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
