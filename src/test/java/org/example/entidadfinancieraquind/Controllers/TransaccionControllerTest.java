package org.example.entidadfinancieraquind.Controllers;

import org.example.entidadfinancieraquind.Entitys.Transaccion;
import org.example.entidadfinancieraquind.Services.TransaccionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TransaccionControllerTest {

    private TransaccionController transaccionController;
    private TransaccionService transaccionService;

    @BeforeEach
    void setUp() {
        transaccionService = mock(TransaccionService.class);
        transaccionController = new TransaccionController(transaccionService);
    }

    @Test
    void obtenerTodasTransacciones_DebeRetornarListaVacia() {
        // Arrange
        when(transaccionService.obtenerTodasTransacciones()).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<List<Transaccion>> response = transaccionController.obtenerTodasTransacciones();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void obtenerTransaccionPorId_Existente_DebeRetornarTransaccion() {
        // Arrange
        long id = 1;
        Transaccion transaccion = new Transaccion();
        transaccion.setId(id);
        when(transaccionService.obtenerTransaccionPorId(id)).thenReturn(Optional.of(transaccion));

        // Act
        ResponseEntity<Transaccion> response = transaccionController.obtenerTransaccionPorId(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transaccion, response.getBody());
    }

    @Test
    void obtenerTransaccionPorId_NoExistente_DebeRetornarNotFound() {
        // Arrange
        long id = 1;
        when(transaccionService.obtenerTransaccionPorId(id)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Transaccion> response = transaccionController.obtenerTransaccionPorId(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void crearTransaccion_TransaccionValida_DebeRetornarTransaccionCreada() {
        // Arrange
        Transaccion transaccion = new Transaccion();
        transaccion.setId(1L);
        when(transaccionService.crearTransaccion(transaccion)).thenReturn(transaccion);

        // Act
        ResponseEntity<Transaccion> response = transaccionController.crearTransaccion(transaccion);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(transaccion, response.getBody());
    }


    @Test
    void actualizarTransaccion_TransaccionExistente_DebeRetornarTransaccionActualizada() {
        // Arrange
        long id = 1;
        Transaccion transaccionActualizada = new Transaccion();
        transaccionActualizada.setId(id);
        when(transaccionService.actualizarTransaccion(id, transaccionActualizada)).thenReturn(transaccionActualizada);

        // Act
        ResponseEntity<Transaccion> response = transaccionController.actualizarTransaccion(id, transaccionActualizada);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transaccionActualizada, response.getBody());
    }

    @Test
    void actualizarTransaccion_TransaccionNoExistente_DebeRetornarNotFound() {
        // Arrange
        long id = 1;
        Transaccion transaccion = new Transaccion();
        transaccion.setId(id);
        when(transaccionService.actualizarTransaccion(id, transaccion)).thenReturn(null);

        // Act
        ResponseEntity<Transaccion> response = transaccionController.actualizarTransaccion(id, transaccion);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void eliminarTransaccion_TransaccionExistente_DebeRetornarNoContent() {
        // Arrange
        long id = 1;

        // Act
        ResponseEntity<Void> response = transaccionController.eliminarTransaccion(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transaccionService, times(1)).eliminarTransaccion(id);
    }
}
