package com.sistema.estudiante._8_proyectoprogramacion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/students")
public class Controlador {

    private List<Estudiante> listaEstudiantes = new ArrayList<>();
    private long siguienteId = 1;

    private boolean correoRepetido(String correo, Long idIgnorar) {
        return listaEstudiantes.stream()
                .anyMatch(e -> e.getCorreo().equalsIgnoreCase(correo)
                        && !e.getId().equals(idIgnorar));
    }

    @GetMapping("")
    public List<Estudiante> obtenerTodos() {
        return listaEstudiantes;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estudiante> obtenerPorId(@PathVariable Long id) {
        return listaEstudiantes.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/idioma/{idioma}")
    public List<Estudiante> obtenerPorIdioma(@PathVariable String idioma) {
        return listaEstudiantes.stream()
                .filter(e -> idioma.equalsIgnoreCase(e.getIdioma()))
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> agregarEstudiante(@Valid @RequestBody Estudiante estudiante) {
        if (correoRepetido(estudiante.getCorreo(), null)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("correo", "Ya existe un estudiante con ese correo"));
        }
        estudiante.setId(siguienteId++);
        listaEstudiantes.add(estudiante);
        return ResponseEntity.status(HttpStatus.CREATED).body(estudiante);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEstudiante(@PathVariable Long id,
                                                  @Valid @RequestBody Estudiante datos) {
        for (Estudiante e : listaEstudiantes) {
            if (e.getId().equals(id)) {
                if (correoRepetido(datos.getCorreo(), id)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(Map.of("correo", "Ya existe un estudiante con ese correo"));
                }
                e.setNombre(datos.getNombre());
                e.setApellido(datos.getApellido());
                e.setCorreo(datos.getCorreo());
                e.setTelefono(datos.getTelefono());
                e.setIdioma(datos.getIdioma());
                return ResponseEntity.ok(e);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editarParcial(@PathVariable Long id, @RequestBody Estudiante datos) {
        for (Estudiante e : listaEstudiantes) {
            if (e.getId().equals(id)) {
                if (datos.getNombre() != null) e.setNombre(datos.getNombre());
                if (datos.getApellido() != null) e.setApellido(datos.getApellido());
                if (datos.getTelefono() != null) e.setTelefono(datos.getTelefono());
                if (datos.getIdioma() != null) e.setIdioma(datos.getIdioma());
                if (datos.getCorreo() != null) {
                    if (correoRepetido(datos.getCorreo(), id)) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(Map.of("correo", "Ya existe un estudiante con ese correo"));
                    }
                    e.setCorreo(datos.getCorreo());
                }
                return ResponseEntity.ok(e);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarEstudiante(@PathVariable Long id) {
        boolean eliminado = listaEstudiantes.removeIf(e -> e.getId().equals(id));
        if (eliminado) {
            return ResponseEntity.ok("Estudiante eliminado con exito");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Estudiante no encontrado");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarErrores(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }
}
