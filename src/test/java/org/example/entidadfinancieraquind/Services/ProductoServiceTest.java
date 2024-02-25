package org.example.entidadfinancieraquind.Services;


import org.example.entidadfinanciera.Entitys.Producto;
import org.example.entidadfinanciera.Repositorys.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void obtenerTodosProductos_DebeRetornarListaVaciaCuandoNoHayProductos() {
        when(productoRepository.findAll()).thenReturn(Collections.emptyList());

        List<Producto> productos = productoService.obtenerTodosProductos();

        assertTrue(productos.isEmpty());
    }

    @Test
    public void obtenerProductoPorId_DebeRetornarProductoCorrectoCuandoExiste() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setTipoCuenta("Cuenta de Ahorros");

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Optional<Producto> productoOptional = productoService.obtenerProductoPorId(1L);

        assertTrue(productoOptional.isPresent());
        assertEquals(producto.getId(), productoOptional.get().getId());
        assertEquals(producto.getTipoCuenta(), productoOptional.get().getTipoCuenta());
    }

    @Test
    public void crearProducto_DebeCrearProductoConFechasDeCreacionYModificacion() {
        Producto producto = new Producto();
        producto.setTipoCuenta("Cuenta de Ahorros");
        producto.setSaldo(1000.0);

        when(productoRepository.save(producto)).thenReturn(producto);

        Producto productoCreado = productoService.crearProducto(producto);

        assertNotNull(productoCreado);
        assertNotNull(productoCreado.getFechaCreacion());
        assertNotNull(productoCreado.getFechaModificacion());
        assertEquals(producto.getTipoCuenta(), productoCreado.getTipoCuenta());
    }

    // Añadir más pruebas según sea necesario para otros métodos del servicio
}
