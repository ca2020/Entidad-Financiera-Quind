package org.example.entidadfinancieraquind.Services;

import org.example.entidadfinancieraquind.Constantes.FinancieraConstantes;
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
        // Restar 19 años a la fecha actual para asegurarse de que el cliente tenga al menos 18 años
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
        // Arrange
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        // Act
        Cliente clienteCreado = clienteService.crearCliente(cliente);

        // Assert
        assertNotNull(clienteCreado);
        assertEquals(cliente.getNombres(), clienteCreado.getNombres());
        assertEquals(cliente.getApellidos(), clienteCreado.getApellidos());
        assertEquals(cliente.getCorreoElectronico(), clienteCreado.getCorreoElectronico());
        assertEquals(cliente.getFechaNacimiento(), clienteCreado.getFechaNacimiento());
    }

    @Test
    void crearCliente_ClienteMenorDe18Anios_DebeLanzarExcepcion() {
        // Arrange
        cliente.setFechaNacimiento(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 17)); // Menos de 18 años
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        // Act & Assert
        assertThrows(EdadInsuficienteException.class, () -> clienteService.crearCliente(cliente));
    }

    @Test
    void actualizarCliente_ClienteExistente_DebeRetornarClienteActualizado() {
        // Arrange
        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setId(1L);
        clienteActualizado.setNombres("Jane");
        clienteActualizado.setApellidos("Doe");
        clienteActualizado.setCorreoElectronico("jane@example.com");
        clienteActualizado.setFechaNacimiento(new Date());

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(cliente)).thenReturn(clienteActualizado);

        // Act
        Cliente clienteModificado = clienteService.actualizarCliente(cliente.getId(), clienteActualizado);

        // Assert
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

        // Act
        Cliente clienteModificado = clienteService.actualizarCliente(cliente.getId(), clienteActualizado);

        // Assert
        assertNull(clienteModificado);
    }

    @Test
    void eliminarCliente_ClienteExistente_DebeEliminarCliente() {
        // Arrange
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));

        // Act
        assertDoesNotThrow(() -> clienteService.eliminarCliente(cliente.getId()));

        // Assert
        verify(clienteRepository, times(1)).deleteById(cliente.getId());
    }

    @Test
    void eliminarCliente_ClienteNoExistente_DebeLanzarExcepcion() {
        // Arrange
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> clienteService.eliminarCliente(cliente.getId()));
    }

    // Otras pruebas para obtenerTodosClientes(), obtenerClientePorId(), etc.

}
