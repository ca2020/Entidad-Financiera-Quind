package org.example.entidadfinancieraquind.Services;


import org.example.entidadfinancieraquind.Entitys.Cliente;
import org.example.entidadfinancieraquind.Entitys.Producto;
import org.example.entidadfinancieraquind.Exceptions.ClienteNotFoundException;
import org.example.entidadfinancieraquind.Exceptions.ProductoNotFoundException;
import org.example.entidadfinancieraquind.Exceptions.SaldoNoCeroException;
import org.example.entidadfinancieraquind.Exceptions.TipoProductoInvalidoException;
import org.example.entidadfinancieraquind.Repositorys.ClienteRepository;
import org.example.entidadfinancieraquind.Repositorys.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    public List<Producto> obtenerTodosProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Producto crearProducto(Producto producto) {
        String tipoProducto = producto.getTipoCuenta();
        String numeroCuenta = generarNumeroCuenta(tipoProducto);
        producto.setNumeroCuenta(numeroCuenta);

        Optional<Cliente> cliente = clienteRepository.findById(producto.getCliente().getId());
        if (!cliente.isPresent()) {
            throw new ClienteNotFoundException("El cliente asociado al producto no existe.");
        }

        if (!tipoProducto.equals("Cuenta Corriente") && !tipoProducto.equals("Cuenta de Ahorros")) {
            throw new TipoProductoInvalidoException("El tipo de producto debe ser 'Cuenta Corriente' o 'Cuenta de Ahorros'.");
        }
        // Aplicar regla de negocio: La cuenta de ahorros no puede tener un saldo menor a $0 (cero)
        if (producto.getTipoCuenta().equalsIgnoreCase("cuenta de ahorros") && producto.getSaldo() < 0) {
            throw new IllegalArgumentException("El saldo de una cuenta de ahorros no puede ser menor de 0.");
        }

        // Aplicar regla de negocio: Al crear una cuenta de ahorro, esta debe establecerse como activa de forma predeterminada
        if (producto.getTipoCuenta().equalsIgnoreCase("cuenta de ahorros")) {
            producto.setEstado("activa");
        }
        // Aplicar regla de negocio para cuentas corrientes: estado aleatorio si saldo > 0
        if (tipoProducto.equalsIgnoreCase("cuenta corriente") && producto.getSaldo() > 0) {
            // Generar un valor aleatorio entre 0 y 1 para determinar el estado
            int randomValue = new Random().nextInt(2);
            if (randomValue == 0) {
                producto.setEstado("activa");
            } else {
                producto.setEstado("inactiva");
            }
        }

        // Establecer fechas de creación y modificación
        producto.setFechaCreacion(new Date());
        producto.setFechaModificacion(new Date());

        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto productoActualizar) {
        return productoRepository.findById(id)
                .map(producto -> {
                    String tipoProducto = productoActualizar.getTipoCuenta();

                    // Verificar si el tipo de producto ha cambiado
                    if (!tipoProducto.equalsIgnoreCase(producto.getTipoCuenta())) {
                        // Generar un nuevo número de cuenta solo si el tipo de producto ha cambiado
                        String numeroCuenta = generarNumeroCuenta(tipoProducto);
                        producto.setNumeroCuenta(numeroCuenta);
                    }

                    if (!tipoProducto.equals("Cuenta Corriente") && !tipoProducto.equals("Cuenta de Ahorros")) {
                        throw new TipoProductoInvalidoException("El tipo de producto debe ser 'Cuenta Corriente' o 'Cuenta de Ahorros'.");
                    }

                    // Aplicar regla de negocio: La cuenta de ahorros no puede tener un saldo menor a $0 (cero)
                    if (tipoProducto.equalsIgnoreCase("cuenta de ahorros") && productoActualizar.getSaldo() < 0) {
                        throw new IllegalArgumentException("El saldo de una cuenta de ahorros no puede ser menor de 0.");
                    }

                    // Aplicar regla de negocio: Al crear una cuenta de ahorro, esta debe establecerse como activa de forma predeterminada
                    if (tipoProducto.equalsIgnoreCase("cuenta de ahorros")) {
                        productoActualizar.setEstado("activa");
                    }

                    // Actualizar el estado de la cuenta de ahorros si su saldo llega a cero
                    if (productoActualizar.getTipoCuenta().equalsIgnoreCase("cuenta de ahorros") && productoActualizar.getSaldo() == 0) {
                        producto.setEstado("cancelado");
                    }

                    // Actualizar el estado de la cuenta corriente si su saldo llega a cero o es mayor que cero
                    if (productoActualizar.getTipoCuenta().equalsIgnoreCase("cuenta corriente")) {
                        if (productoActualizar.getSaldo() == 0) {
                            producto.setEstado("cancelado");
                        } else {
                            // Generar un valor aleatorio entre 0 y 1 para determinar el estado
                            int randomValue = new Random().nextInt(2);
                            producto.setEstado(randomValue == 0 ? "activa" : "inactiva");
                        }
                    }
                    producto.setTipoCuenta(tipoProducto);
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

    private String generarNumeroCuenta(String tipoProducto) {
        // Generar un número de cuenta único de 10 dígitos
        String numeroCuenta = generarNumeroAleatorio();

        // Verificar el tipo de producto y ajustar el prefijo del número de cuenta
        if (tipoProducto.equalsIgnoreCase("Cuenta Corriente")) {
            numeroCuenta = "33" + numeroCuenta.substring(0, 8); // Agregar "33" al inicio para cuentas corrientes
        } else if (tipoProducto.equalsIgnoreCase("Cuenta de Ahorros")) {
            numeroCuenta = "53" + numeroCuenta.substring(0, 8); // Agregar "53" al inicio para cuentas de ahorros
        }

        return numeroCuenta;
    }

    private String generarNumeroAleatorio() {
        // Generar un número aleatorio de 10 dígitos
        Random random = new Random();
        int numeroAleatorio = random.nextInt(900000000) + 100000000; // Números entre 100000000 y 999999999
        return String.valueOf(numeroAleatorio);
    }
    public void cancelarCuenta(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("No se encontró el producto con el ID especificado."));

        // Verificar que el saldo sea igual a $0
        if (producto.getSaldo() == 0) {
            // Cambiar el estado de la cuenta a "cancelada"
            producto.setEstado("cancelada");
            productoRepository.save(producto);
        } else {
            throw new SaldoNoCeroException("No se puede cancelar la cuenta porque el saldo no es igual a $0.");
        }
    }

}

