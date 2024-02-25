package org.example.entidadfinancieraquind.Controllers;

import org.example.entidadfinanciera.Entitys.Cliente;
import org.example.entidadfinanciera.Services.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void obtenerTodosClientes_DebeRetornarListaDeClientes() {
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        cliente1.setNombres("Juan");
        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNombres("María");

        List<Cliente> clientes = Arrays.asList(cliente1, cliente2);

        when(clienteService.obtenerTodosClientes()).thenReturn(clientes);

        ResponseEntity<List<Cliente>> responseEntity = clienteController.obtenerTodosClientes();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(clientes, responseEntity.getBody());
    }

    @Test
    public void obtenerClientePorId_DebeRetornarClienteExistente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombres("Juan");

        when(clienteService.obtenerClientePorId(1L)).thenReturn(Optional.of(cliente));

        ResponseEntity<Cliente> responseEntity = clienteController.obtenerClientePorId(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(cliente, responseEntity.getBody());
    }

    @Test
    public void obtenerClientePorId_DebeRetornarNotFoundParaClienteNoExistente() {
        when(clienteService.obtenerClientePorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<Cliente> responseEntity = clienteController.obtenerClientePorId(1L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void crearCliente_DebeCrearNuevoCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombres("Juan");

        when(clienteService.crearCliente(cliente)).thenReturn(cliente);

        ResponseEntity<Cliente> responseEntity = clienteController.crearCliente(cliente);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(cliente, responseEntity.getBody());
    }

    @Test
    public void actualizarCliente_DebeActualizarClienteExistente() {
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNombres("Juan");

        when(clienteService.actualizarCliente(id, cliente)).thenReturn(cliente);

        ResponseEntity<Cliente> responseEntity = clienteController.actualizarCliente(id, cliente);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(cliente, responseEntity.getBody());
    }

    @Test
    public void actualizarCliente_DebeRetornarNotFoundParaClienteNoExistente() {
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNombres("Juan");

        when(clienteService.actualizarCliente(id, cliente)).thenReturn(null);

        ResponseEntity<Cliente> responseEntity = clienteController.actualizarCliente(id, cliente);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void eliminarCliente_DebeEliminarClienteExistente() {
        Long id = 1L;

        ResponseEntity<String> responseEntity = clienteController.eliminarCliente(id);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(clienteService, times(1)).eliminarCliente(id);
    }
}
