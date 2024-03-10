package org.example.entidadfinancieraquind.Entitys;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@Table(name = "transacciones")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El tipo de transacción es requerido")
    private String tipo;

    @NotNull(message = "El monto de la transacción es requerido")
    private double monto;

    @NotNull(message = "La fecha de creación es requerida")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "cuenta_origen_id")
    private Producto cuentaOrigen;

    @ManyToOne
    @JoinColumn(name = "cuenta_destino_id")
    private Producto cuentaDestino;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

}
