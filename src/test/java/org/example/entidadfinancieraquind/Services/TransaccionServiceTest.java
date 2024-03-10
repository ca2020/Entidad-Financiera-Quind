package org.example.entidadfinancieraquind.Services;

import org.example.entidadfinancieraquind.Constantes.FinancieraConstantes;
import org.example.entidadfinancieraquind.Entitys.*;
import org.example.entidadfinancieraquind.Exceptions.SaldoInsuficienteException;
import org.example.entidadfinancieraquind.Repositorys.MovimientoCreditoRepository;
import org.example.entidadfinancieraquind.Repositorys.MovimientoDebitoRepository;
import org.example.entidadfinancieraquind.Repositorys.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private ProductoService productoService;

    @Mock
    private MovimientoDebitoRepository movimientoDebitoRepository;
    @Mock
    private MovimientoCreditoRepository movimientoCreditoRepository;


    @InjectMocks
    private TransaccionService transaccionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void crearTransaccionConsignacion() {

        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(FinancieraConstantes.CONSIGNACION);
        transaccion.setMonto(100.0);
        Producto cuentaDestino = new Producto();
        cuentaDestino.setId(1L);
        transaccion.setCuentaDestino(cuentaDestino);

        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.ofNullable(cuentaDestino));


        Transaccion resultado = transaccionService.crearTransaccion(transaccion);


        verify(transaccionRepository, times(1)).save(transaccion);
    }


    @Test
    void crearTransaccionRetiroSaldoSuficiente() {

        Producto cuentaOrigen = new Producto();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(200.0);
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(FinancieraConstantes.RETIRO);
        transaccion.setMonto(100.0);
        transaccion.setCuentaOrigen(cuentaOrigen);

        Transaccion transaccionSimulada = new Transaccion();
        transaccionSimulada.setTipo(FinancieraConstantes.RETIRO);

        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.ofNullable(cuentaOrigen));

        when(transaccionService.crearTransaccion(transaccion)).thenReturn(transaccionSimulada);

        Transaccion resultado = transaccionService.crearTransaccion(transaccion);

        assertNotNull(resultado);

        assertEquals(FinancieraConstantes.RETIRO, resultado.getTipo());
        assertEquals(0.0, cuentaOrigen.getSaldo());
        verify(transaccionRepository, times(1)).save(transaccion);
    }





    @Test
    void crearTransaccionRetiroSaldoInsuficiente() {
        Producto cuentaOrigen = new Producto();
        cuentaOrigen.setId(1L);
        cuentaOrigen.setSaldo(50.0);
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(FinancieraConstantes.RETIRO);
        transaccion.setMonto(100.0);
        transaccion.setCuentaOrigen(cuentaOrigen);

        when(productoService.obtenerProductoPorId(1L)).thenReturn(Optional.of(cuentaOrigen));


        assertThrows(SaldoInsuficienteException.class, () -> transaccionService.crearTransaccion(transaccion));


        verify(transaccionRepository, never()).save(transaccion);
    }

}
