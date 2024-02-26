package org.example.entidadfinancieraquind.Services;


import jakarta.transaction.Transactional;
import org.example.entidadfinancieraquind.Constantes.FinancieraConstantes;
import org.example.entidadfinancieraquind.Entitys.Cliente;
import org.example.entidadfinancieraquind.Exceptions.EdadInsuficienteException;
import org.example.entidadfinancieraquind.Repositorys.ClienteRepository;
import org.example.entidadfinancieraquind.Repositorys.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ProductoRepository productoRepository;

    public List<Cliente> obtenerTodosClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente crearCliente(@Valid Cliente cliente) {
        validarCorreoElectronico(cliente.getCorreoElectronico());
        validarLongitudNombre(cliente.getNombres());
        validarLongitudApellido(cliente.getApellidos());

        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaNacimiento = cliente.getFechaNacimiento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long edadEnAnios = ChronoUnit.YEARS.between(fechaNacimiento, fechaActual);
        if (edadEnAnios < 18) {
            throw new EdadInsuficienteException(FinancieraConstantes.CLIENTE_MAYOR_EDAD);
        }

        cliente.setFechaCreacion(new Date());
        cliente.setFechaModificacion(new Date());
        return clienteRepository.save(cliente);
    }

    public Cliente actualizarCliente(Long id, Cliente clienteActualizar) {
        return clienteRepository.findById(id)
                .map(cliente -> {

                    cliente.setTipoIdentificacion(clienteActualizar.getTipoIdentificacion());
                    cliente.setNumeroIdentificacion(clienteActualizar.getNumeroIdentificacion());
                    cliente.setCorreoElectronico(validarCorreoElectronico(clienteActualizar.getCorreoElectronico()));
                    cliente.setNombres(validarLongitudNombre(clienteActualizar.getNombres()));
                    cliente.setApellidos(validarLongitudApellido(clienteActualizar.getApellidos()));
                    cliente.setFechaNacimiento(clienteActualizar.getFechaNacimiento());
                    cliente.setFechaModificacion(new Date());

                    return clienteRepository.save(cliente);
                })
                .orElse(null);
    }

    @Transactional
    public void eliminarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(FinancieraConstantes.CLIENTE_NO_ENCONTRADO_CON_ID));

        if (productoRepository.existsByCliente(cliente)) {
            throw new IllegalStateException(FinancieraConstantes.CLIENTE_VINCULO_PRODUCTO);
        }

        clienteRepository.deleteById(id);
    }

    private @Email(message = "El correo electrónico debe tener un formato válido") String validarCorreoElectronico(String correoElectronico) {
        // Utilizar una expresión regular para verificar el formato del correo electrónico
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!correoElectronico.matches(regex)) {
            throw new IllegalArgumentException(FinancieraConstantes.ERROR_FORMATO_EMAIL);
        }
        return correoElectronico;
    }

    private @NotBlank(message = "Los nombres son requeridos") @Size(min = 2, message = "El nombre debe tener al menos 2 caracteres") String validarLongitudNombre(String nombre) {
        if (nombre.length() < 2) {
            throw new IllegalArgumentException(FinancieraConstantes.ERROR_CARACTERES_NOMBRE);
        }
        return nombre;
    }

    private @NotBlank(message = "Los apellidos son requeridos") @Size(min = 2, message = "El apellido debe tener al menos 2 caracteres") String validarLongitudApellido(String apellido) {
        if (apellido.length() < 2) {
            throw new IllegalArgumentException(FinancieraConstantes.ERROR_CARACTERES_APELLIDO);
        }
        return apellido;
    }
}

