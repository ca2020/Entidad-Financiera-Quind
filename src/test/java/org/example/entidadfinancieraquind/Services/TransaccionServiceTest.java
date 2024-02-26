package org.example.entidadfinancieraquind.Services;

import org.example.entidadfinancieraquind.Constantes.FinancieraConstantes;
import org.example.entidadfinancieraquind.Entitys.*;
import org.example.entidadfinancieraquind.Exceptions.SaldoInsuficienteException;
import org.example.entidadfinancieraquind.Repositorys.MovimientoCreditoRepository;
import org.example.entidadfinancieraquind.Repositorys.MovimientoDebitoRepository;
import org.example.entidadfinancieraquind.Repositorys.TransaccionRepository;
import org.example.entidadfinancieraquind.Services.ProductoService;
import org.example.entidadfinancieraquind.Services.TransaccionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private MovimientoCreditoRepository movimientoCreditoRepository;

    @Mock
    private MovimientoDebitoRepository movimientoDebitoRepository;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private TransaccionService transaccionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void crearTransaccionConsignacion() {
        // Creamos una instancia de Transaccion para la prueba
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(FinancieraConstantes.CONSIGNACION);
        transaccion.setMonto(100.0);
        Producto cuentaDestino = new Producto();
        cuentaDestino.setId(1L);
        transaccion.setCuentaDestino(cuentaDestino);

        // Configuramos el comportamiento simulado del productoService para devolver un Optional con cuentaDestino cuando se llame a obtenerProductoPorId con el ID correspondiente
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.ofNullable(cuentaDestino));

        // Invocamos al método de la transacciónService que queremos probar
        Transaccion resultado = transaccionService.crearTransaccion(transaccion);

        // Verificamos que se llamó al método save del repositorio con la transacción como argumento
        verify(transaccionRepository, times(1)).save(transaccion); // Verificamos que se llamó al método save del repositorio una vez
    }



    // Pruebas para los otros tipos de transacción y casos de prueba adicionales...

    @Test
    void crearTransaccionRetiroSaldoSuficiente() {
        // Test de retiro con saldo suficiente
        // Simular el comportamiento del producto
        Producto cuentaOrigen = new Producto();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(200.0);
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(FinancieraConstantes.RETIRO);
        transaccion.setMonto(100.0);
        transaccion.setCuentaOrigen(cuentaOrigen);

        // Crear un objeto Transaccion simulado
        Transaccion transaccionSimulada = new Transaccion();
        transaccionSimulada.setTipo(FinancieraConstantes.RETIRO);

        // Configurar el comportamiento del servicio de producto
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.ofNullable(cuentaOrigen));

        // Configurar el comportamiento del servicio de transaccionService para devolver la transacción simulada
        when(transaccionService.crearTransaccion(transaccion)).thenReturn(transaccionSimulada);

        // Ejecutar el método y verificar que se guarde en el repositorio de transacciones
        Transaccion resultado = transaccionService.crearTransaccion(transaccion);

        // Verificar que el resultado no sea null antes de realizar más verificaciones
        assertNotNull(resultado);

        // Verificar otras condiciones si es necesario
        assertEquals(FinancieraConstantes.RETIRO, resultado.getTipo());
        assertEquals(0.0, cuentaOrigen.getSaldo());
        verify(transaccionRepository, times(1)).save(transaccion);
    }





    @Test
    void crearTransaccionRetiroSaldoInsuficiente() {
        // Test de retiro con saldo insuficiente
        // Simular el comportamiento del producto
        Producto cuentaOrigen = new Producto();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(50.0);
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(FinancieraConstantes.RETIRO);
        transaccion.setMonto(100.0);
        transaccion.setCuentaOrigen(cuentaOrigen);

        // Configurar el comportamiento del servicio de producto
        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(cuentaOrigen));

        // Ejecutar el método y verificar que se lance la excepción adecuada
        assertThrows(SaldoInsuficienteException.class, () -> transaccionService.crearTransaccion(transaccion));

        // Verificar que no se guarde la transacción
        verify(transaccionRepository, never()).save(transaccion);
    }

    // Pruebas para los otros métodos y casos de prueba adicionales...
}
