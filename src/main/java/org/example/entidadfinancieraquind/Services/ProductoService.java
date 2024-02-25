package org.example.entidadfinancieraquind.Services;


import org.example.entidadfinancieraquind.Entitys.Producto;
import org.example.entidadfinancieraquind.Repositorys.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodosProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto crearProducto(Producto producto) {
        // Aplicar regla de negocio: La cuenta de ahorros no puede tener un saldo menor a $0 (cero)
        if (producto.getTipoCuenta().equalsIgnoreCase("cuenta de ahorros") && producto.getSaldo() < 0) {
            throw new IllegalArgumentException("El saldo de una cuenta de ahorros no puede ser negativo.");
        }

        // Aplicar regla de negocio: Al crear una cuenta de ahorro, esta debe establecerse como activa de forma predeterminada
        if (producto.getTipoCuenta().equalsIgnoreCase("cuenta de ahorros")) {
            producto.setEstado("activa");
        }

        // Establecer fechas de creación y modificación
        producto.setFechaCreacion(new Date());
        producto.setFechaModificacion(new Date());

        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto productoActualizar) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setTipoCuenta(productoActualizar.getTipoCuenta());
                    producto.setNumeroCuenta(productoActualizar.getNumeroCuenta());
                    producto.setEstado(productoActualizar.getEstado());
                    producto.setSaldo(productoActualizar.getSaldo());
                    producto.setExentaGMF(productoActualizar.isExentaGMF());
                    producto.setFechaModificacion(new Date());
                    return productoRepository.save(producto);
                })
                .orElse(null);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }
}

