package org.example.entidadfinancieraquind.Exceptions;

public class SaldoNoCeroException extends RuntimeException {
    public SaldoNoCeroException(String message) {
        super(message);
    }
}
