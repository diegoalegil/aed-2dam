package aula.tareas.controller;

import java.net.URI;
import java.util.List;


import aula.tareas.modelo.Tarea;
import aula.tareas.service.TareaServiceCsv;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/csv/tareas")
class TareaControllerCsv {
    private final TareaServiceCsv servicio;

    TareaControllerCsv(@Qualifier("tareaServiceCsv") TareaServiceCsv servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    List<Tarea> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    ResponseEntity<Tarea> buscar(@PathVariable long id) {
        return ResponseEntity.ofNullable(servicio.buscar(id));
    }

    @PostMapping
    ResponseEntity<Tarea> crear(@Valid @RequestBody @NonNull NuevaTarea peticion) {
        Tarea tarea = servicio.crear(peticion.titulo());
        return ResponseEntity.created(URI.create("/api/tareas/" + tarea.id())).body(tarea);
    }

    @PutMapping("/{id}")
    ResponseEntity<Tarea> actualizar (@PathVariable long id, @Valid @RequestBody NuevaTarea peticion) {
        return ResponseEntity.ofNullable(servicio.actualizar(id,peticion.titulo()));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> borrar (@PathVariable long id) {
        if (servicio.borrar(id)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    public record NuevaTarea(@NotBlank String titulo) {
    }
}
