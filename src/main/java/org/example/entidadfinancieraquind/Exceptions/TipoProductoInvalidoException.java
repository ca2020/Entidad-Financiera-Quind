package org.example.entidadfinancieraquind.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TipoProductoInvalidoException extends RuntimeException {
    public TipoProductoInvalidoException(String message) {
        super(message);
    }
}
