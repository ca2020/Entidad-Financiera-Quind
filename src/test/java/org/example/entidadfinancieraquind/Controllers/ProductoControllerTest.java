package org.example.entidadfinancieraquind.Controllers;

import org.example.entidadfinancieraquind.Constantes.FinancieraConstantes;
import org.example.entidadfinancieraquind.Entitys.Producto;
import org.example.entidadfinancieraquind.Exceptions.ClienteNotFoundException;
import org.example.entidadfinancieraquind.Exceptions.TipoProductoInvalidoException;
import org.example.entidadfinancieraquind.Services.ProductoService;
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

import static org.junit.jupiter.api.Assertions.*;
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
        producto1.setTipoCuenta("Cuenta de Ahorros");
        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setTipoCuenta("Cuenta Corriente");

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
        producto.setTipoCuenta("Cuenta de Ahorros");

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
        producto.setTipoCuenta("Cuenta de Ahorros");

        when(productoService.crearProducto(producto)).thenReturn(producto);

        ResponseEntity<Object> responseEntity = productoController.crearProducto(producto);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(producto, responseEntity.getBody());
    }

    @Test
    public void crearProducto_DebeRetornarBadRequestParaClienteNoEncontrado() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setTipoCuenta("Cuenta de Ahorros");

        when(productoService.crearProducto(producto)).thenThrow(ClienteNotFoundException.class);

        ResponseEntity<Object> responseEntity = productoController.crearProducto(producto);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(FinancieraConstantes.ERROR_AL_CREAR_EL_PRODUCTO, responseEntity.getBody());
    }

    @Test
    public void crearProducto_DebeRetornarBadRequestParaOtroError() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setTipoCuenta("Cuenta de Ahorros");

        String errorMessage = "Error al crear el producto";

        when(productoService.crearProducto(producto)).thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<Object> responseEntity = productoController.crearProducto(producto);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(errorMessage, responseEntity.getBody());
    }


    @Test
    public void crearProducto_DebeRetornarBadRequestParaTipoProductoInvalido() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setTipoCuenta("Tipo Inválido");

        String errorMessage = "Mensaje de error específico para el tipo de producto inválido";

        when(productoService.crearProducto(producto)).thenThrow(new TipoProductoInvalidoException(errorMessage));

        ResponseEntity<Object> responseEntity = productoController.crearProducto(producto);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(errorMessage, responseEntity.getBody());
    }



    @Test
    public void actualizarProducto_DebeActualizarProductoExistente() {
        Long id = 1L;
        Producto producto = new Producto();
        producto.setId(id);
        producto.setTipoCuenta("Cuenta de Ahorros");

        when(productoService.actualizarProducto(id, producto)).thenReturn(producto);

        ResponseEntity<Object> responseEntity = productoController.actualizarProducto(id, producto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(producto, responseEntity.getBody());
    }

    @Test
    public void actualizarProducto_DebeRetornarNotFoundParaProductoNoExistente() {
        Long id = 1L;
        Producto producto = new Producto();
        producto.setId(id);
        producto.setTipoCuenta("Cuenta de Ahorros");

        when(productoService.actualizarProducto(id, producto)).thenReturn(null);

        ResponseEntity<Object> responseEntity = productoController.actualizarProducto(id, producto);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void actualizarProducto_DebeCancelarCuentaDeAhorrosConSaldoCero() {
        Long id = 1L;
        Producto producto = new Producto();
        producto.setId(id);
        producto.setTipoCuenta("Cuenta de Ahorros");
        producto.setSaldo(0);

        when(productoService.actualizarProducto(id, producto)).thenReturn(producto);

        ResponseEntity<Object> responseEntity = productoController.actualizarProducto(id, producto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(FinancieraConstantes.CANCELADA, producto.getEstado());
    }

    @Test
    public void actualizarProducto_DebeCambiarEstadoDeCuentaCorriente() {
        Long id = 1L;
        Producto producto = new Producto();
        producto.setId(id);
        producto.setTipoCuenta("Cuenta Corriente");
        producto.setSaldo(1000);

        when(productoService.actualizarProducto(id, producto)).thenReturn(producto);

        ResponseEntity<Object> responseEntity = productoController.actualizarProducto(id, producto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(producto.getEstado().equals(FinancieraConstantes.ACTIVA) || producto.getEstado().equals(FinancieraConstantes.INACTIVA));
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
