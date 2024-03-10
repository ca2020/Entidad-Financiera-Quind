package org.example.entidadfinancieraquind.Exceptions;


public class ProductoNotFoundException extends RuntimeException {
    public ProductoNotFoundException(String message) {
        super(message);
    }
}