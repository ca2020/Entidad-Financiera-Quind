package org.example.entidadfinancieraquind.Services;


import org.example.entidadfinancieraquind.Constantes.FinancieraConstantes;
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
    private static final Random RANDOM = new Random();
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
            throw new ClienteNotFoundException(FinancieraConstantes.ERROR_CLIENTE_ASOCIADO_PRODUCTO);
        }

        if (!tipoProducto.equals(FinancieraConstantes.CUENTA_CORRIENTE) && !tipoProducto.equals(FinancieraConstantes.CUENTA_AHORROS)) {
            throw new TipoProductoInvalidoException(FinancieraConstantes.ERROR_AHORROS_CORRIENTE);
        }
        // Aplicar regla de negocio: La cuenta de ahorros no puede tener un saldo menor a $0 (cero)
        if (producto.getTipoCuenta().equalsIgnoreCase(FinancieraConstantes.CUENTA_AHORROS) && producto.getSaldo() < 0) {
            throw new IllegalArgumentException(FinancieraConstantes.ERROR_SALDO_AHORRO_CERO);
        }

        // Aplicar regla de negocio: Al crear una cuenta de ahorro, esta debe establecerse como activa de forma predeterminada
        if (producto.getTipoCuenta().equalsIgnoreCase(FinancieraConstantes.CUENTA_AHORROS)) {
            producto.setEstado(FinancieraConstantes.ACTIVA);
        }
        // Aplicar regla de negocio para cuentas corrientes: estado aleatorio si saldo > 0
        if (tipoProducto.equalsIgnoreCase(FinancieraConstantes.CUENTA_CORRIENTE) && producto.getSaldo() > 0) {
            // Generar un valor aleatorio entre 0 y 1 para determinar el estado
            int randomValue = RANDOM.nextInt(2);
            if (randomValue == 0) {
                producto.setEstado(FinancieraConstantes.ACTIVA);
            } else {
                producto.setEstado(FinancieraConstantes.INACTIVA);
            }
        }

        // Establecer fechas de creación y modificación
        producto.setFechaCreacion(new Date());
        producto.setFechaModificacion(new Date());

        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto productoActualizar) {
        // Buscar el producto en el repositorio
        Optional<Producto> productoOptional = productoRepository.findById(id);

        // Verificar si el producto existe en el repositorio
        if (productoOptional.isEmpty()) {
            throw new ProductoNotFoundException(FinancieraConstantes.PRODUCTO_NO_ENCONTRADO_CON_ID + id);
        }

        // Obtener el producto de la base de datos
        Producto producto = productoOptional.get();

        // Actualizar el producto con la información proporcionada
        actualizarInformacionProducto(producto, productoActualizar);

        // Guardar y devolver el producto actualizado
        return productoRepository.save(producto);
    }

    private void actualizarInformacionProducto(Producto producto, Producto productoActualizar) {
        // Verificar y actualizar el tipo de cuenta si ha cambiado
        actualizarTipoCuenta(producto, productoActualizar);

        // Verificar y aplicar reglas de negocio según el tipo de cuenta
        aplicarReglasNegocio(producto, productoActualizar);
    }

    private void actualizarTipoCuenta(Producto producto, Producto productoActualizar) {
        String tipoProducto = productoActualizar.getTipoCuenta();

        // Verificar si el tipo de cuenta ha cambiado
        if (!tipoProducto.equalsIgnoreCase(producto.getTipoCuenta())) {
            // Generar un nuevo número de cuenta si el tipo de cuenta ha cambiado
            String numeroCuenta = generarNumeroCuenta(tipoProducto);
            producto.setNumeroCuenta(numeroCuenta);
        }

        // Actualizar el tipo de cuenta del producto
        producto.setTipoCuenta(tipoProducto);
    }

    private void aplicarReglasNegocio(Producto producto, Producto productoActualizar) {
        String tipoCuenta = productoActualizar.getTipoCuenta();
        double saldo = productoActualizar.getSaldo();

        // Verificar y aplicar reglas de negocio según el tipo de cuenta
        if (tipoCuenta.equalsIgnoreCase(FinancieraConstantes.CUENTA_AHORROS)) {
            // Aplicar reglas de negocio para cuentas de ahorros
            aplicarReglasAhorros(producto, saldo);
        } else if (tipoCuenta.equalsIgnoreCase(FinancieraConstantes.CUENTA_CORRIENTE)) {
            // Aplicar reglas de negocio para cuentas corrientes
            aplicarReglasCorriente(producto, saldo);
        } else {
            throw new TipoProductoInvalidoException(FinancieraConstantes.ERROR_AHORROS_CORRIENTE);
        }
    }

    private void aplicarReglasAhorros(Producto producto, double saldo) {
        // Verificar si el saldo de la cuenta de ahorros es negativo
        if (saldo < 0) {
            throw new IllegalArgumentException(FinancieraConstantes.ERROR_SALDO_AHORRO_CERO);
        }

        // Establecer el estado de la cuenta de ahorros como activa
        producto.setEstado(FinancieraConstantes.ACTIVA);

        // Actualizar el estado de la cuenta de ahorros si el saldo llega a cero
        if (saldo == 0) {
            producto.setEstado(FinancieraConstantes.CANCELADA);
        }
    }

    private void aplicarReglasCorriente(Producto producto, double saldo) {
        // Verificar si el saldo de la cuenta corriente es cero o mayor que cero
        if (saldo == 0) {
            producto.setEstado(FinancieraConstantes.CANCELADA);
        } else {
            // Generar un valor aleatorio entre 0 y 1 para determinar el estado
            int randomValue = RANDOM.nextInt(2);
            producto.setEstado(randomValue == 0 ? FinancieraConstantes.ACTIVA : FinancieraConstantes.INACTIVA);
        }
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    private String generarNumeroCuenta(String tipoProducto) {
        // Generar un número de cuenta único de 10 dígitos
        String numeroCuenta = generarNumeroAleatorio();

        // Verificar el tipo de producto y ajustar el prefijo del número de cuenta
        if (tipoProducto.equalsIgnoreCase(FinancieraConstantes.CUENTA_CORRIENTE)) {
            numeroCuenta = FinancieraConstantes.N_CUENTA_CORRIENTE + numeroCuenta.substring(0, 8); // Agregar "33" al inicio para cuentas corrientes
        } else if (tipoProducto.equalsIgnoreCase(FinancieraConstantes.CUENTA_AHORROS)) {
            numeroCuenta = FinancieraConstantes.N_CUENTA_AHORROS + numeroCuenta.substring(0, 8); // Agregar "53" al inicio para cuentas de ahorros
        }

        return numeroCuenta;
    }

    // Método para generar un número aleatorio de 10 dígitos
    private String generarNumeroAleatorio() {
        int numeroAleatorio = RANDOM.nextInt(900000000) + 100000000; // Números entre 100000000 y 999999999
        return String.valueOf(numeroAleatorio);
    }
    public void cancelarCuenta(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(FinancieraConstantes.PRODUCTO_NO_ENCONTRADO_CON_ID));

        // Verificar que el saldo sea igual a $0
        if (producto.getSaldo() == 0) {
            // Cambiar el estado de la cuenta a "cancelada"
            producto.setEstado(FinancieraConstantes.CANCELADA);
            productoRepository.save(producto);
        } else {
            throw new SaldoNoCeroException(FinancieraConstantes.ERROR_CANCELAR_CUENTA);
        }
    }

}

