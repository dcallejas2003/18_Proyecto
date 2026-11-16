package com.sistema.estudiante._8_proyectoprogramacion;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Estudiante {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede pasar de 255 caracteres")
    private String nombre;

    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El teléfono debe tener exactamente 8 dígitos")
    private String telefono;

    @NotBlank(message = "El idioma es obligatorio")
    @Pattern(regexp = "inglés|español|francés|todos", message = "Idioma permitido: inglés, español, francés, todos")
    private String idioma;
}
