package org.example.entidadfinancieraquind.Services;


import org.example.entidadfinancieraquind.Entitys.Producto;
import org.example.entidadfinancieraquind.Entitys.Transaccion;
import org.example.entidadfinancieraquind.Exceptions.SaldoInsuficienteException;
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
    private ProductoService productoService; // Inyectamos el servicio de Producto

    public List<Transaccion> obtenerTodasTransacciones() {
        return transaccionRepository.findAll();
    }

    public Optional<Transaccion> obtenerTransaccionPorId(Long id) {
        return transaccionRepository.findById(id);
    }

    public Transaccion crearTransaccion(Transaccion transaccion) {
        Producto producto = productoService.obtenerProductoPorId(transaccion.getProducto().getId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el producto asociado a la transacción."));

        // Verificar si el saldo disponible es suficiente para realizar la transacción
        if (producto.getSaldo() >= transaccion.getMonto()) {
            // Actualizar el saldo de la cuenta después de la transacción
            double nuevoSaldo = producto.getSaldo() - transaccion.getMonto();
            producto.setSaldo(nuevoSaldo);
            productoService.actualizarProducto(producto.getId(), producto);

            // Establecer la fecha de creación de la transacción
            transaccion.setFechaCreacion(new Date());

            // Guardar la transacción en la base de datos
            return transaccionRepository.save(transaccion);
        } else {
            // Si el saldo no es suficiente, se puede lanzar una excepción o manejar el error de alguna otra manera
            throw new SaldoInsuficienteException("Saldo insuficiente para realizar la transacción.");
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

