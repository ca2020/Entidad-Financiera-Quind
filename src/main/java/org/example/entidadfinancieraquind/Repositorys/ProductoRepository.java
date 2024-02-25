package org.example.entidadfinancieraquind.Repositorys;


import org.example.entidadfinancieraquind.Entitys.Cliente;
import org.example.entidadfinancieraquind.Entitys.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Aquí puedes agregar métodos adicionales de ser necesario
    boolean existsByCliente(Cliente cliente);
}

