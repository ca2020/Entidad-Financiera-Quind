package org.example.entidadfinancieraquind.Entitys;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;


@Data
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El tipo de identificación es requerido")
    @Column(name = "tipo_identificacion")
    private String tipoIdentificacion;

    @NotBlank(message = "El número de identificación es requerido")
    @Pattern(regexp = "\\d{7,14}", message = "El número de identificación debe tener entre 7 y 14 dígitos")
    @Column(name = "numero_identificacion")
    private String numeroIdentificacion;

    @NotBlank(message = "Los nombres son requeridos")
    @Size(min = 2, message = "El nombre debe tener al menos 2 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son requeridos")
    @Size(min = 2, message = "El apellido debe tener al menos 2 caracteres")
    private String apellidos;

    @Email(message = "El correo electrónico debe tener un formato válido")
    @Column(name = "correo_electronico")
    private String correoElectronico;

    @NotNull(message = "La fecha de nacimiento es requerida")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Column(name = "fecha_nacimiento")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaNacimiento;

    @NotNull(message = "La fecha de creación es requerida")
    @Past(message = "La fecha de creación debe ser en el pasado")
    @Column(name = "fecha_creacion")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaCreacion;

    @NotNull(message = "La fecha de modificación es requerida")
    @Past(message = "La fecha de modificación debe ser en el pasado")
    @Column(name = "fecha_modificacion")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaModificacion;

}


