package org.example.entidadfinancieraquind.Services;


import org.example.entidadfinancieraquind.Constantes.FinancieraConstantes;
import org.example.entidadfinancieraquind.Entitys.MovimientoCredito;
import org.example.entidadfinancieraquind.Entitys.MovimientoDebito;
import org.example.entidadfinancieraquind.Entitys.Producto;
import org.example.entidadfinancieraquind.Entitys.Transaccion;
import org.example.entidadfinancieraquind.Exceptions.SaldoInsuficienteException;
import org.example.entidadfinancieraquind.Repositorys.MovimientoCreditoRepository;
import org.example.entidadfinancieraquind.Repositorys.MovimientoDebitoRepository;
import org.example.entidadfinancieraquind.Repositorys.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransaccionService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private MovimientoCreditoRepository movimientoCreditoRepository;

    @Autowired
    private MovimientoDebitoRepository movimientoDebitoRepository;

    @Autowired
    private ProductoService productoService;

    public List<Transaccion> obtenerTodasTransacciones() {
        return transaccionRepository.findAll();
    }

    public Optional<Transaccion> obtenerTransaccionPorId(Long id) {
        return transaccionRepository.findById(id);
    }

    public Transaccion crearTransaccion(Transaccion transaccion) {

        transaccion.setFechaCreacion(new Date());


        switch (transaccion.getTipo()) {
            case FinancieraConstantes.CONSIGNACION:
                return realizarConsignacion(transaccion);
            case FinancieraConstantes.RETIRO:
                return realizarRetiro(transaccion);
            case FinancieraConstantes.TRANSFERENCIA:
                return realizarTransferencia(transaccion);
            default:
                throw new IllegalArgumentException(FinancieraConstantes.ERROR_TIPO_TRANSACCION_INVALIDA);
        }
    }

    private Transaccion realizarConsignacion(Transaccion transaccion) {
        Optional<Producto> optionalCuentaDestino = productoService.obtenerProductoPorId(transaccion.getCuentaDestino().getId());
        Producto cuentaDestino = optionalCuentaDestino.orElseThrow(() -> new IllegalArgumentException(FinancieraConstantes.ERROR_CUENTA_DESTINO_NO_EXISTE));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo() + transaccion.getMonto());
        productoService.actualizarProducto(cuentaDestino.getId(), cuentaDestino);

        return transaccionRepository.save(transaccion);
    }

    private Transaccion realizarRetiro(Transaccion transaccion) {
        Optional<Producto> optionalCuentaOrigen = productoService.obtenerProductoPorId(transaccion.getCuentaOrigen().getId());
        Producto cuentaOrigen = optionalCuentaOrigen.orElseThrow(() -> new IllegalArgumentException(FinancieraConstantes.ERROR_CUENTA_ORIGEN_NO_EXISTE));

        if (cuentaOrigen.getSaldo() >= transaccion.getMonto()) {
            cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - transaccion.getMonto());
            productoService.actualizarProducto(cuentaOrigen.getId(), cuentaOrigen);

            return transaccionRepository.save(transaccion);
        } else {
            throw new SaldoInsuficienteException(FinancieraConstantes.ERROR_SALDO_INSUFICIENTE_RETIRO);
        }
    }

    public Transaccion realizarTransferencia(Transaccion transaccion) {
        Optional<Producto> optionalCuentaOrigen = productoService.obtenerProductoPorId(transaccion.getCuentaOrigen().getId());
        Optional<Producto> optionalCuentaDestino = productoService.obtenerProductoPorId(transaccion.getCuentaDestino().getId());

        Producto cuentaOrigen = optionalCuentaOrigen.orElseThrow(() -> new IllegalArgumentException(FinancieraConstantes.ERROR_CUENTA_ORIGEN_NO_EXISTE));
        Producto cuentaDestino = optionalCuentaDestino.orElseThrow(() -> new IllegalArgumentException(FinancieraConstantes.ERROR_CUENTA_DESTINO_NO_EXISTE));

        if (cuentaOrigen.getSaldo() >= transaccion.getMonto()) {
            cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - transaccion.getMonto());
            productoService.actualizarProducto(cuentaOrigen.getId(), cuentaOrigen);

            cuentaDestino.setSaldo(cuentaDestino.getSaldo() + transaccion.getMonto());
            productoService.actualizarProducto(cuentaDestino.getId(), cuentaDestino);
            MovimientoDebito movimientoDebito = new MovimientoDebito();
            movimientoDebito.setCuenta(cuentaOrigen);
            movimientoDebito.setMonto(transaccion.getMonto());
            movimientoDebito.setFecha(new Date());
            movimientoDebitoRepository.save(movimientoDebito);

            MovimientoCredito movimientoCredito = new MovimientoCredito();
            movimientoCredito.setCuenta(cuentaDestino);
            movimientoCredito.setMonto(transaccion.getMonto());
            movimientoCredito.setFecha(new Date());
            movimientoCreditoRepository.save(movimientoCredito);

            if (cuentaOrigen.getTipoCuenta().equalsIgnoreCase(FinancieraConstantes.CUENTA_AHORROS) && cuentaOrigen.getSaldo() == 0) {

                cuentaOrigen.setEstado(FinancieraConstantes.CANCELADA);
                productoService.actualizarProducto(cuentaOrigen.getId(), cuentaOrigen);
            }
            return transaccionRepository.save(transaccion);
        } else {
            throw new SaldoInsuficienteException(FinancieraConstantes.ERROR_SALDO_INSUFICIENTE_TRANSFERENCIA);
        }
    }


    public Transaccion actualizarTransaccion(Long id, Transaccion transaccionActualizar) {
        Transaccion transaccionExistente = transaccionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(FinancieraConstantes.TRANSACCION_NO_ENCONTRADO_CON_ID));
        transaccionExistente.setTipo(transaccionActualizar.getTipo());
        transaccionExistente.setMonto(transaccionActualizar.getMonto());
        return transaccionRepository.save(transaccionExistente);
    }

    public void eliminarTransaccion(Long id) {
        transaccionRepository.deleteById(id);
    }

}


