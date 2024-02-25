package org.example.entidadfinancieraquind.Services;


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
    private ProductoService productoService; // Inyectamos el servicio de Producto

    public List<Transaccion> obtenerTodasTransacciones() {
        return transaccionRepository.findAll();
    }

    public Optional<Transaccion> obtenerTransaccionPorId(Long id) {
        return transaccionRepository.findById(id);
    }

    public Transaccion crearTransaccion(Transaccion transaccion) {
        // Establecer la fecha de creación de la transacción
        transaccion.setFechaCreacion(new Date());

        // Realizar la transacción según el tipo
        switch (transaccion.getTipo()) {
            case "Consignación":
                return realizarConsignacion(transaccion);
            case "Retiro":
                return realizarRetiro(transaccion);
            case "Transferencia":
                return realizarTransferencia(transaccion);
            default:
                throw new IllegalArgumentException("Tipo de transacción inválido.");
        }
    }

    private Transaccion realizarConsignacion(Transaccion transaccion) {
        // Obtener la cuenta de destino desde la base de datos utilizando su ID
        Optional<Producto> optionalCuentaDestino = productoService.obtenerProductoPorId(transaccion.getCuentaDestino().getId());

        // Desempaquetar el Optional para obtener la cuenta de destino
        Producto cuentaDestino = optionalCuentaDestino.orElseThrow(() -> new IllegalArgumentException("La cuenta de destino no existe en el sistema."));

        // Realizar la consignación
        cuentaDestino.setSaldo(cuentaDestino.getSaldo() + transaccion.getMonto());
        productoService.actualizarProducto(cuentaDestino.getId(), cuentaDestino);

        return transaccionRepository.save(transaccion);
    }

    private Transaccion realizarRetiro(Transaccion transaccion) {
        // Obtener la cuenta de origen desde la base de datos utilizando su ID
        Optional<Producto> optionalCuentaOrigen = productoService.obtenerProductoPorId(transaccion.getCuentaOrigen().getId());

        // Desempaquetar el Optional para obtener la cuenta de origen
        Producto cuentaOrigen = optionalCuentaOrigen.orElseThrow(() -> new IllegalArgumentException("La cuenta de origen no existe en el sistema."));

        // Realizar el retiro
        if (cuentaOrigen.getSaldo() >= transaccion.getMonto()) {
            cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - transaccion.getMonto());
            productoService.actualizarProducto(cuentaOrigen.getId(), cuentaOrigen);

            return transaccionRepository.save(transaccion);
        } else {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar el retiro.");
        }
    }

    private Transaccion realizarTransferencia(Transaccion transaccion) {
        // Obtener la cuenta de origen y destino desde la base de datos utilizando sus IDs
        Optional<Producto> optionalCuentaOrigen = productoService.obtenerProductoPorId(transaccion.getCuentaOrigen().getId());
        Optional<Producto> optionalCuentaDestino = productoService.obtenerProductoPorId(transaccion.getCuentaDestino().getId());

        // Desempaquetar los Optionals para obtener las cuentas de origen y destino
        Producto cuentaOrigen = optionalCuentaOrigen.orElseThrow(() -> new IllegalArgumentException("La cuenta de origen no existe en el sistema."));
        Producto cuentaDestino = optionalCuentaDestino.orElseThrow(() -> new IllegalArgumentException("La cuenta de destino no existe en el sistema."));

        // Realizar la transferencia
        if (cuentaOrigen.getSaldo() >= transaccion.getMonto()) {
            // Realizar el débito en la cuenta de origen
            cuentaOrigen.setSaldo(cuentaOrigen.getSaldo() - transaccion.getMonto());
            productoService.actualizarProducto(cuentaOrigen.getId(), cuentaOrigen);

            // Realizar el crédito en la cuenta de destino
            cuentaDestino.setSaldo(cuentaDestino.getSaldo() + transaccion.getMonto());
            productoService.actualizarProducto(cuentaDestino.getId(), cuentaDestino);
            // Generar movimientos de débito y crédito
            MovimientoDebito movimientoDebito = new MovimientoDebito();
            movimientoDebito.setCuenta(cuentaOrigen);
            movimientoDebito.setMonto(transaccion.getMonto());
            movimientoDebito.setFecha(new Date());
            movimientoDebitoRepository.save(movimientoDebito); // Guardar el movimiento de débito en la base de datos

            MovimientoCredito movimientoCredito = new MovimientoCredito();
            movimientoCredito.setCuenta(cuentaDestino);
            movimientoCredito.setMonto(transaccion.getMonto());
            movimientoCredito.setFecha(new Date());
            movimientoCreditoRepository.save(movimientoCredito); // Guardar el movimiento de crédito en la base de datos


            // Verificar si la cuenta de origen es una cuenta de ahorros y su saldo ha llegado a cero
            if (cuentaOrigen.getTipoCuenta().equalsIgnoreCase("cuenta de ahorros") && cuentaOrigen.getSaldo() == 0) {
                // Actualizar el estado de la cuenta a "cancelado"
                cuentaOrigen.setEstado("cancelado");
                productoService.actualizarProducto(cuentaOrigen.getId(), cuentaOrigen);
            }

            return transaccionRepository.save(transaccion);
        } else {
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar la transferencia.");
        }
    }


    public Transaccion actualizarTransaccion(Long id, Transaccion transaccionActualizar) {
        Transaccion transaccionExistente = transaccionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la transacción con el ID proporcionado."));

        // Actualizar los campos de la transacción existente con los datos proporcionados en transaccionActualizar
        transaccionExistente.setTipo(transaccionActualizar.getTipo());
        transaccionExistente.setMonto(transaccionActualizar.getMonto());

        // Puedes seguir actualizando los demás campos según sea necesario

        return transaccionRepository.save(transaccionExistente);
    }

    public void eliminarTransaccion(Long id) {
        transaccionRepository.deleteById(id);
    }

    // Otros métodos de actualización, eliminación y consulta según sea necesario
}


