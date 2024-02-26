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

        if (producto.getTipoCuenta().equalsIgnoreCase(FinancieraConstantes.CUENTA_AHORROS) && producto.getSaldo() < 0) {
            throw new IllegalArgumentException(FinancieraConstantes.ERROR_SALDO_AHORRO_CERO);
        }

        if (producto.getTipoCuenta().equalsIgnoreCase(FinancieraConstantes.CUENTA_AHORROS)) {
            producto.setEstado(FinancieraConstantes.ACTIVA);
        }
        if (tipoProducto.equalsIgnoreCase(FinancieraConstantes.CUENTA_CORRIENTE) && producto.getSaldo() > 0) {
            int randomValue = RANDOM.nextInt(2);
            if (randomValue == 0) {
                producto.setEstado(FinancieraConstantes.ACTIVA);
            } else {
                producto.setEstado(FinancieraConstantes.INACTIVA);
            }
        }

        producto.setFechaCreacion(new Date());
        producto.setFechaModificacion(new Date());

        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto productoActualizar) {

        Optional<Producto> productoOptional = productoRepository.findById(id);

        if (productoOptional.isEmpty()) {
            throw new ProductoNotFoundException(FinancieraConstantes.PRODUCTO_NO_ENCONTRADO_CON_ID + id);
        }

        Producto producto = productoOptional.get();

        actualizarInformacionProducto(producto, productoActualizar);

        return productoRepository.save(producto);
    }

    private void actualizarInformacionProducto(Producto producto, Producto productoActualizar) {
        actualizarTipoCuenta(producto, productoActualizar);

        aplicarReglasNegocio(producto, productoActualizar);
    }

    private void actualizarTipoCuenta(Producto producto, Producto productoActualizar) {
        String tipoProducto = productoActualizar.getTipoCuenta();

        if (!tipoProducto.equalsIgnoreCase(producto.getTipoCuenta())) {
            String numeroCuenta = generarNumeroCuenta(tipoProducto);
            producto.setNumeroCuenta(numeroCuenta);
        }

        producto.setTipoCuenta(tipoProducto);
    }

    private void aplicarReglasNegocio(Producto producto, Producto productoActualizar) {
        String tipoCuenta = productoActualizar.getTipoCuenta();
        double saldo = productoActualizar.getSaldo();

        if (tipoCuenta.equalsIgnoreCase(FinancieraConstantes.CUENTA_AHORROS)) {
            aplicarReglasAhorros(producto, saldo);
        } else if (tipoCuenta.equalsIgnoreCase(FinancieraConstantes.CUENTA_CORRIENTE)) {
            aplicarReglasCorriente(producto, saldo);
        } else {
            throw new TipoProductoInvalidoException(FinancieraConstantes.ERROR_AHORROS_CORRIENTE);
        }
    }

    private void aplicarReglasAhorros(Producto producto, double saldo) {
        if (saldo < 0) {
            throw new IllegalArgumentException(FinancieraConstantes.ERROR_SALDO_AHORRO_CERO);
        }

        producto.setEstado(FinancieraConstantes.ACTIVA);

        if (saldo == 0) {
            producto.setEstado(FinancieraConstantes.CANCELADA);
        }
    }

    private void aplicarReglasCorriente(Producto producto, double saldo) {
        if (saldo == 0) {
            producto.setEstado(FinancieraConstantes.CANCELADA);
        } else {
            int randomValue = RANDOM.nextInt(2);
            producto.setEstado(randomValue == 0 ? FinancieraConstantes.ACTIVA : FinancieraConstantes.INACTIVA);
        }
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    private String generarNumeroCuenta(String tipoProducto) {

        String numeroCuenta = generarNumeroAleatorio();
        if (tipoProducto.equalsIgnoreCase(FinancieraConstantes.CUENTA_CORRIENTE)) {
            numeroCuenta = FinancieraConstantes.N_CUENTA_CORRIENTE + numeroCuenta.substring(0, 8);
        } else if (tipoProducto.equalsIgnoreCase(FinancieraConstantes.CUENTA_AHORROS)) {
            numeroCuenta = FinancieraConstantes.N_CUENTA_AHORROS + numeroCuenta.substring(0, 8);
        }

        return numeroCuenta;
    }

    private String generarNumeroAleatorio() {
        int numeroAleatorio = RANDOM.nextInt(900000000) + 100000000;
        return String.valueOf(numeroAleatorio);
    }
    public void cancelarCuenta(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(FinancieraConstantes.PRODUCTO_NO_ENCONTRADO_CON_ID));


        if (producto.getSaldo() == 0) {
            producto.setEstado(FinancieraConstantes.CANCELADA);
            productoRepository.save(producto);
        } else {
            throw new SaldoNoCeroException(FinancieraConstantes.ERROR_CANCELAR_CUENTA);
        }
    }

}

