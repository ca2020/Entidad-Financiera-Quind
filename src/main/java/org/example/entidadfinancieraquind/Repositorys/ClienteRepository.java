package org.example.entidadfinancieraquind.Repositorys;


import org.example.entidadfinancieraquind.Entitys.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Aquí puedes agregar métodos adicionales de ser necesario
}

