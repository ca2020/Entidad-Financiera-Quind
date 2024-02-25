package org.example.entidadfinancieraquind.Entitys;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El tipo de cuenta es requerido")
    @Pattern(regexp = "cuenta corriente|cuenta de ahorros", message = "El tipo de cuenta debe ser 'cuenta corriente' o 'cuenta de ahorros'")
    @Column(name = "tipo_cuenta")
    private String tipoCuenta;

    @NotBlank(message = "El número de cuenta es requerido")
    @Pattern(regexp = "\\d{10}", message = "El número de cuenta debe tener 10 dígitos numéricos")
    @Column(name = "numero_cuenta")
    private String numeroCuenta;

    @NotBlank(message = "El estado es requerido")
    private String estado;

    @NotNull(message = "El saldo es requerido")
    private double saldo;

    @Column(name = "exenta_gmf")
    private boolean exentaGMF;

    @NotNull(message = "La fecha de creación es requerida")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @NotNull(message = "La fecha de modificación es requerida")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_modificacion")
    private Date fechaModificacion;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // Otros campos y métodos
}