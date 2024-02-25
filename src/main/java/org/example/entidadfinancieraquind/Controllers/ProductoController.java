package org.example.entidadfinancieraquind.Controllers;



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
            // Log de la excepción en la consola
            System.err.println("Error al crear el producto: " + e.getMessage());
            // Devolución de un mensaje claro al usuario en el JSON de respuesta
            return ResponseEntity.badRequest().body("Error al crear el producto: El cliente asociado no existe.");
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

            // Verificar si el producto fue actualizado correctamente
            if (productoActualizado != null) {
                // Verificar si el saldo es igual a $0 y el tipo de cuenta es "Cuenta de Ahorros"
                if (productoActualizado.getSaldo() == 0 && productoActualizado.getTipoCuenta().equalsIgnoreCase("Cuenta de Ahorros")) {
                    productoService.cancelarCuenta(productoActualizado.getId());
                    productoActualizado.setEstado("cancelada");
                } else if (productoActualizado.getTipoCuenta().equalsIgnoreCase("Cuenta Corriente")) {
                    // Establecer el estado de la cuenta corriente según su saldo
                    if (productoActualizado.getSaldo() == 0) {
                        productoActualizado.setEstado("cancelada");
                    } else {
                        // Generar un valor aleatorio entre 0 y 1 para determinar el estado
                        int randomValue = new Random().nextInt(2);
                        productoActualizado.setEstado(randomValue == 0 ? "activa" : "inactiva");
                    }
                }
                return ResponseEntity.ok(productoActualizado);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (TipoProductoInvalidoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}

