package org.example.entidadfinancieraquind.Services;

import org.example.entidadfinancieraquind.Constantes.FinancieraConstantes;
import org.example.entidadfinancieraquind.Entitys.*;
import org.example.entidadfinancieraquind.Exceptions.ClienteNotFoundException;
import org.example.entidadfinancieraquind.Exceptions.SaldoNoCeroException;
import org.example.entidadfinancieraquind.Repositorys.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ClienteRepository clienteRepository;


    @InjectMocks
    private ProductoService productoService;


    private Producto producto;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setTipoCuenta(FinancieraConstantes.CUENTA_CORRIENTE);
        producto.setCliente(new Cliente());
        producto.setSaldo(1000.0);
        producto.setExentaGMF(false);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombres("John");
        cliente.setApellidos("Doe");
        cliente.setCorreoElectronico("john@example.com");
        cliente.setFechaNacimiento(new Date());
    }

    @Test
    void obtenerTodosProductos() {
        when(productoRepository.findAll()).thenReturn(List.of(producto));
        assertEquals(1, productoService.obtenerTodosProductos().size());
    }

    @Test
    void obtenerProductoPorId_existente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        Optional<Producto> productoOptional = productoService.obtenerProductoPorId(1L);
        assertTrue(productoOptional.isPresent());
        assertEquals(producto.getId(), productoOptional.get().getId());
    }

    @Test
    void obtenerProductoPorId_noExistente() {
        when(productoRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Producto> productoOptional = productoService.obtenerProductoPorId(2L);
        assertTrue(productoOptional.isEmpty());
    }

    @Test
    void crearProducto() {
        when(clienteRepository.findById(any())).thenReturn(Optional.of(cliente));
        when(productoRepository.save(any())).thenReturn(producto);

        Producto productoCreado = productoService.crearProducto(producto);
        assertNotNull(productoCreado);
        assertEquals(producto.getTipoCuenta(), productoCreado.getTipoCuenta());
    }

    @Test
    void crearProducto_clienteNoExiste() {
        when(clienteRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ClienteNotFoundException.class, () -> productoService.crearProducto(producto));
    }



    @Test
    void actualizarProducto_existente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any())).thenReturn(producto);

        Producto productoActualizado = new Producto();
        productoActualizado.setId(1L);
        productoActualizado.setTipoCuenta(FinancieraConstantes.CUENTA_CORRIENTE);
        productoActualizado.setCliente(new Cliente());
        productoActualizado.setSaldo(1000.0);
        productoActualizado.setExentaGMF(true);

        Producto productoResultado = productoService.actualizarProducto(1L, productoActualizado);
        assertNotNull(productoResultado);
        assertEquals(productoActualizado.getSaldo(), productoResultado.getSaldo());
    }



    @Test
    void eliminarProducto() {
        assertDoesNotThrow(() -> productoService.eliminarProducto(1L));
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void cancelarCuenta_saldoCero() {
        producto.setSaldo(0.0);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        assertDoesNotThrow(() -> productoService.cancelarCuenta(1L));
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void cancelarCuenta_saldoNoCero() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        producto.setSaldo(1000.0);
        assertThrows(SaldoNoCeroException.class, () -> productoService.cancelarCuenta(1L));
    }



}
