package org.example.entidadfinancieraquind.Repositorys;

import org.example.entidadfinancieraquind.Entitys.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    // Aquí puedes agregar métodos adicionales de ser necesario
}

