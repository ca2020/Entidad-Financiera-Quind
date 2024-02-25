package org.example.entidadfinancieraquind.Controllers;

import org.example.entidadfinanciera.Entitys.Transaccion;
import org.example.entidadfinanciera.Services.TransaccionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class TransaccionControllerTest {

    @Mock
    private TransaccionService transaccionService;

    @InjectMocks
    private TransaccionController transaccionController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void obtenerTodasTransacciones_DebeRetornarListaDeTransacciones() {
        Transaccion transaccion1 = new Transaccion();
        transaccion1.setId(1L);
        transaccion1.setTipo("Transaccion 1");
        Transaccion transaccion2 = new Transaccion();
        transaccion2.setId(2L);
        transaccion2.setTipo("Transaccion 2");

        List<Transaccion> transacciones = Arrays.asList(transaccion1, transaccion2);

        when(transaccionService.obtenerTodasTransacciones()).thenReturn(transacciones);

        ResponseEntity<List<Transaccion>> responseEntity = transaccionController.obtenerTodasTransacciones();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(transacciones, responseEntity.getBody());
    }

    @Test
    public void obtenerTransaccionPorId_DebeRetornarTransaccionExistente() {
        Transaccion transaccion = new Transaccion();
        transaccion.setId(1L);
        transaccion.setTipo("Transaccion 1");

        when(transaccionService.obtenerTransaccionPorId(1L)).thenReturn(Optional.of(transaccion));

        ResponseEntity<Transaccion> responseEntity = transaccionController.obtenerTransaccionPorId(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(transaccion, responseEntity.getBody());
    }

    @Test
    public void obtenerTransaccionPorId_DebeRetornarNotFoundParaTransaccionNoExistente() {
        when(transaccionService.obtenerTransaccionPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Transaccion> responseEntity = transaccionController.obtenerTransaccionPorId(1L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void crearTransaccion_DebeCrearNuevaTransaccion() {
        Transaccion transaccion = new Transaccion();
        transaccion.setId(1L);
        transaccion.setTipo("Transaccion 1");

        when(transaccionService.crearTransaccion(transaccion)).thenReturn(transaccion);

        ResponseEntity<Transaccion> responseEntity = transaccionController.crearTransaccion(transaccion);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(transaccion, responseEntity.getBody());
    }
}
