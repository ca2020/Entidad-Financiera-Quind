package org.example.entidadfinancieraquind.Controllers;

import org.example.entidadfinanciera.Entitys.Producto;
import org.example.entidadfinanciera.Services.ProductoService;
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
import static org.mockito.Mockito.*;

public class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void obtenerTodosProductos_DebeRetornarListaDeProductos() {
        Producto producto1 = new Producto();
        producto1.setId(1L);
        Producto producto2 = new Producto();
        producto2.setId(2L);

        List<Producto> productos = Arrays.asList(producto1, producto2);

        when(productoService.obtenerTodosProductos()).thenReturn(productos);

        ResponseEntity<List<Producto>> responseEntity = productoController.obtenerTodosProductos();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(productos, responseEntity.getBody());
    }

    @Test
    public void obtenerProductoPorId_DebeRetornarProductoExistente() {
        Producto producto = new Producto();
        producto.setId(1L);

        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto));

        ResponseEntity<Producto> responseEntity = productoController.obtenerProductoPorId(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(producto, responseEntity.getBody());
    }

    @Test
    public void obtenerProductoPorId_DebeRetornarNotFoundParaProductoNoExistente() {
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Producto> responseEntity = productoController.obtenerProductoPorId(1L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void crearProducto_DebeCrearNuevoProducto() {
        Producto producto = new Producto();
        producto.setId(1L);

        when(productoService.crearProducto(producto)).thenReturn(producto);

        ResponseEntity<Producto> responseEntity = productoController.crearProducto(producto);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(producto, responseEntity.getBody());
    }

    @Test
    public void actualizarProducto_DebeActualizarProductoExistente() {
        Long id = 1L;
        Producto producto = new Producto();
        producto.setId(id);

        when(productoService.actualizarProducto(id, producto)).thenReturn(producto);

        ResponseEntity<Producto> responseEntity = productoController.actualizarProducto(id, producto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(producto, responseEntity.getBody());
    }

    @Test
    public void actualizarProducto_DebeRetornarNotFoundParaProductoNoExistente() {
        Long id = 1L;
        Producto producto = new Producto();
        producto.setId(id);

        when(productoService.actualizarProducto(id, producto)).thenReturn(null);

        ResponseEntity<Producto> responseEntity = productoController.actualizarProducto(id, producto);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void eliminarProducto_DebeEliminarProductoExistente() {
        Long id = 1L;

        ResponseEntity<Void> responseEntity = productoController.eliminarProducto(id);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(productoService, times(1)).eliminarProducto(id);
    }
}
