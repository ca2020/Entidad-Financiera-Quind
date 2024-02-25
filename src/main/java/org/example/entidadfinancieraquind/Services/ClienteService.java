package org.example.entidadfinancieraquind.Services;


import org.example.entidadfinancieraquind.Entitys.Cliente;
import org.example.entidadfinancieraquind.Exceptions.EdadInsuficienteException;
import org.example.entidadfinancieraquind.Repositorys.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
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
        // Obtener la fecha actual
        LocalDate fechaActual = LocalDate.now();

        // Convertir la fecha de nacimiento del cliente a LocalDate
        LocalDate fechaNacimiento = cliente.getFechaNacimiento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Calcular la diferencia en años entre la fecha actual y la fecha de nacimiento
        long edadEnAnios = ChronoUnit.YEARS.between(fechaNacimiento, fechaActual);

        // Verificar si la edad es menor de 18 años
        if (edadEnAnios < 18) {
            throw new EdadInsuficienteException("El cliente debe ser mayor de 18 años para crear una cuenta.");
        }

        // Continúa con la lógica para guardar el cliente en la base de datos
        cliente.setFechaCreacion(new Date());
        cliente.setFechaModificacion(new Date());
        return clienteRepository.save(cliente);
    }

    public Cliente actualizarCliente(Long id, Cliente clienteActualizar) {
        return clienteRepository.findById(id)
                .map(cliente -> {

                    cliente.setTipoIdentificacion(clienteActualizar.getTipoIdentificacion());
                    cliente.setNumeroIdentificacion(clienteActualizar.getNumeroIdentificacion());
                    validarCorreoElectronico(clienteActualizar.getCorreoElectronico());
                    validarLongitudNombre(clienteActualizar.getNombres());
                    validarLongitudApellido(clienteActualizar.getApellidos());
                    cliente.setFechaNacimiento(clienteActualizar.getFechaNacimiento());
                    cliente.setFechaModificacion(new Date());
                    return clienteRepository.save(cliente);
                })
                .orElse(null);
    }

    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }

    private void validarCorreoElectronico(String correoElectronico) {
        // Utilizar una expresión regular para verificar el formato del correo electrónico
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!correoElectronico.matches(regex)) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }
    }

    private void validarLongitudNombre(String nombre) {
        if (nombre.length() < 2) {
            throw new IllegalArgumentException("El nombre debe tener al menos 2 caracteres.");
        }
    }

    private void validarLongitudApellido(String apellido) {
        if (apellido.length() < 2) {
            throw new IllegalArgumentException("El apellido debe tener al menos 2 caracteres.");
        }
    }
}

