package org.example.entidadfinancieraquind.Controllers;



import org.example.entidadfinancieraquind.Constantes.FinancieraConstantes;
import org.example.entidadfinancieraquind.Entitys.Producto;
import org.example.entidadfinancieraquind.Exceptions.ClienteNotFoundException;
import org.example.entidadfinancieraquind.Exceptions.TipoProductoInvalidoException;
import org.example.entidadfinancieraquind.Services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodosProductos() {
        List<Producto> productos = productoService.obtenerTodosProductos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        Optional<Producto> producto = productoService.obtenerProductoPorId(id);
        return producto.map(value -> ResponseEntity.ok().body(value))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> crearProducto(@RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.crearProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (ClienteNotFoundException e) {
            return ResponseEntity.badRequest().body(FinancieraConstantes.ERROR_AL_CREAR_EL_PRODUCTO);
        } catch (TipoProductoInvalidoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizarProducto(@PathVariable Long id, @RequestBody Producto productoActualizar) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(id, productoActualizar);

            if (productoActualizado == null) {
                return ResponseEntity.notFound().build();
            }

            if (esCuentaDeAhorros(productoActualizado)) {
                cancelarCuentaSiNecesario(productoActualizado);
            } else if (esCuentaCorriente(productoActualizado)) {
                actualizarEstadoCuentaCorriente(productoActualizado);
            }

            return ResponseEntity.ok(productoActualizado);
        } catch (TipoProductoInvalidoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private boolean esCuentaDeAhorros(Producto producto) {
        return producto.getTipoCuenta().equalsIgnoreCase(FinancieraConstantes.CUENTA_AHORROS);
    }

    private boolean esCuentaCorriente(Producto producto) {
        return producto.getTipoCuenta().equalsIgnoreCase(FinancieraConstantes.CUENTA_CORRIENTE);
    }

    private void cancelarCuentaSiNecesario(Producto producto) {
        if (producto.getSaldo() == 0) {
            productoService.cancelarCuenta(producto.getId());
            producto.setEstado(FinancieraConstantes.CANCELADA);
        }
    }

    private void actualizarEstadoCuentaCorriente(Producto producto) {
        if (producto.getSaldo() == 0) {
            producto.setEstado(FinancieraConstantes.CANCELADA);
        } else {
            int randomValue = new Random().nextInt(2);
            producto.setEstado(randomValue == 0 ? FinancieraConstantes.ACTIVA : FinancieraConstantes.INACTIVA);
        }
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}

