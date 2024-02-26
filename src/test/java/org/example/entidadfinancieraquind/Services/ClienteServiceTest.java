package org.example.entidadfinancieraquind.Services;


import org.example.entidadfinancieraquind.Entitys.Cliente;
import org.example.entidadfinancieraquind.Exceptions.EdadInsuficienteException;
import org.example.entidadfinancieraquind.Repositorys.ClienteRepository;
import org.example.entidadfinancieraquind.Repositorys.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {

        LocalDate fechaNacimiento = LocalDate.now().minusYears(19);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombres("John");
        cliente.setApellidos("Doe");
        cliente.setCorreoElectronico("john@example.com");
        cliente.setFechaNacimiento(Date.from(fechaNacimiento.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }


    @Test
    void crearCliente_ClienteValido_DebeRetornarClienteCreado() {

        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente clienteCreado = clienteService.crearCliente(cliente);

        assertNotNull(clienteCreado);
        assertEquals(cliente.getNombres(), clienteCreado.getNombres());
        assertEquals(cliente.getApellidos(), clienteCreado.getApellidos());
        assertEquals(cliente.getCorreoElectronico(), clienteCreado.getCorreoElectronico());
        assertEquals(cliente.getFechaNacimiento(), clienteCreado.getFechaNacimiento());
    }

    @Test
    void crearCliente_ClienteMenorDe18Anios_DebeLanzarExcepcion() {

        cliente.setFechaNacimiento(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 17));
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        assertThrows(EdadInsuficienteException.class, () -> clienteService.crearCliente(cliente));
    }

    @Test
    void actualizarCliente_ClienteExistente_DebeRetornarClienteActualizado() {
        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setId(1L);
        clienteActualizado.setNombres("Jane");
        clienteActualizado.setApellidos("Doe");
        clienteActualizado.setCorreoElectronico("jane@example.com");
        clienteActualizado.setFechaNacimiento(new Date());

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(cliente)).thenReturn(clienteActualizado);

        Cliente clienteModificado = clienteService.actualizarCliente(cliente.getId(), clienteActualizado);

        assertNotNull(clienteModificado);
        assertEquals(clienteActualizado.getNombres(), clienteModificado.getNombres());
        assertEquals(clienteActualizado.getApellidos(), clienteModificado.getApellidos());
        assertEquals(clienteActualizado.getCorreoElectronico(), clienteModificado.getCorreoElectronico());
        assertEquals(clienteActualizado.getFechaNacimiento(), clienteModificado.getFechaNacimiento());
    }

    @Test
    void actualizarCliente_ClienteNoExistente_DebeRetornarNull() {
        // Arrange
        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setId(1L);
        clienteActualizado.setNombres("Jane");
        clienteActualizado.setApellidos("Doe");
        clienteActualizado.setCorreoElectronico("jane@example.com");
        clienteActualizado.setFechaNacimiento(new Date());

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());

        Cliente clienteModificado = clienteService.actualizarCliente(cliente.getId(), clienteActualizado);

        assertNull(clienteModificado);
    }

    @Test
    void eliminarCliente_ClienteExistente_DebeEliminarCliente() {

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));


        assertDoesNotThrow(() -> clienteService.eliminarCliente(cliente.getId()));


        verify(clienteRepository, times(1)).deleteById(cliente.getId());
    }

    @Test
    void eliminarCliente_ClienteNoExistente_DebeLanzarExcepcion() {

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());


        assertThrows(IllegalArgumentException.class, () -> clienteService.eliminarCliente(cliente.getId()));
    }



}
