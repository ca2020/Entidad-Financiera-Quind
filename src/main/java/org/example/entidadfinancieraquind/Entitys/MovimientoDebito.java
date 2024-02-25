package org.example.entidadfinancieraquind.Entitys;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class MovimientoDebito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Producto cuenta;

    private Double monto;

    private Date fecha;

    // Constructor, getters y setters
}
