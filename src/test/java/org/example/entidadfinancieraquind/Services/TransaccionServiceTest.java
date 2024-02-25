package org.example.entidadfinancieraquind.Services;

import org.example.entidadfinanciera.Entitys.Producto;
import org.example.entidadfinanciera.Entitys.Transaccion;
import org.example.entidadfinanciera.Exceptions.SaldoInsuficienteException;
import org.example.entidadfinanciera.Repositorys.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private TransaccionService transaccionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void crearTransaccion_DebeCrearTransaccionCorrectamente() {
        // Crear una instancia de Producto
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setSaldo(200.0);

        // Crear una instancia de Transaccion
        Transaccion transaccion = new Transaccion();
        transaccion.setId(1L);
        transaccion.setTipo("Transferencia");
        transaccion.setMonto(100.0);
        transaccion.setFechaCreacion(new Date());
        // Establecer el producto en la transacción
        transaccion.setProducto(producto);

        // Configurar el mock de productoService para que devuelva un producto válido
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto));

        // Configurar el mock de transaccionRepository para devolver la transacción proporcionada
        when(transaccionRepository.save(transaccion)).thenReturn(transaccion);

        // Ejecutar el método que se está probando
        Transaccion transaccionCreada = transaccionService.crearTransaccion(transaccion);

        // Verificar que la transacción creada no sea nula y contenga los valores esperados
        assertNotNull(transaccionCreada);
        assertEquals(transaccion.getId(), transaccionCreada.getId());
        assertEquals(transaccion.getTipo(), transaccionCreada.getTipo());
        assertEquals(transaccion.getMonto(), transaccionCreada.getMonto());
        assertEquals(transaccion.getFechaCreacion(), transaccionCreada.getFechaCreacion());
    }


    @Test
    public void crearTransaccion_DebeLanzarExcepcionCuandoSaldoInsuficiente() {
        Transaccion transaccion = new Transaccion();
        transaccion.setId(1L);
        transaccion.setTipo("Transferencia");
        transaccion.setMonto(300.0); // Este monto excede el saldo disponible
        // Establecer un producto asociado a la transacción
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setSaldo(200.0);
        transaccion.setProducto(producto);

        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(producto));

        assertThrows(SaldoInsuficienteException.class, () -> {
            transaccionService.crearTransaccion(transaccion);
        });
    }


    // Añadir más pruebas según sea necesario para otros casos de prueba
}
