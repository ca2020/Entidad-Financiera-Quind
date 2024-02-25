package org.example.entidadfinancieraquind.Services;

import org.example.entidadfinanciera.Entitys.Cliente;
import org.example.entidadfinanciera.Repositorys.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void obtenerTodosClientes_DebeRetornarListaVaciaCuandoNoHayClientes() {
        when(clienteRepository.findAll()).thenReturn(Collections.emptyList());

        List<Cliente> clientes = clienteService.obtenerTodosClientes();

        assertTrue(clientes.isEmpty());
    }

    @Test
    public void obtenerClientePorId_DebeRetornarClienteCorrectoCuandoExiste() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombres("Carlos");
        cliente.setApellidos("Salcedo");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Optional<Cliente> clienteOptional = clienteService.obtenerClientePorId(1L);

        assertTrue(clienteOptional.isPresent());
        assertEquals(cliente.getId(), clienteOptional.get().getId());
        assertEquals(cliente.getNombres(), clienteOptional.get().getNombres());
        assertEquals(cliente.getApellidos(), clienteOptional.get().getApellidos());
    }

    @Test
    public void crearCliente_DebeCrearClienteConFechasDeCreacionYModificacion() {
        Cliente cliente = new Cliente();
        cliente.setFechaNacimiento(new Date(System.currentTimeMillis() - 18L * 365 * 24 * 60 * 60 * 1000)); // 18 años de edad
        cliente.setCorreoElectronico("carlos@gmail.com");

        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente clienteCreado = clienteService.crearCliente(cliente);

        assertNotNull(clienteCreado);
        assertNotNull(clienteCreado.getFechaCreacion());
        assertNotNull(clienteCreado.getFechaModificacion());
        assertEquals(cliente.getCorreoElectronico(), clienteCreado.getCorreoElectronico());
    }

    // Añadir más pruebas según sea necesario para otros métodos del servicio
}
