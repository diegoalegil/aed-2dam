package aula.tareas.controller;

import aula.tareas.api.TareaRequest;
import aula.tareas.service.ITareaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/jdbc/tareas")
@Tag(name = "Tareas Jdbc", description = "Operaciones CRUD de tareas en sqlite con Jdbc")
public class TareaControllerJdbc {

    private final ITareaService serviceJdbc;

    public TareaControllerJdbc(@Qualifier("tareaServiceJdbc") ITareaService serviceJdbc) {
        this.serviceJdbc = serviceJdbc;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las tareas desde la tabla tareas_jdbc")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Se obtiene el listado de tareas"))
    List<TareaRequest> listar() {
        return serviceJdbc.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener la tarea a traves de su id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Se obtiene la tarea indicada a traves de su id"),
            @ApiResponse(responseCode = "404", description = "No existe ninguna tarea con ese id")
    })
    ResponseEntity<TareaRequest> buscar(@PathVariable long id) {
        return ResponseEntity.ofNullable(serviceJdbc.buscar(id));
    }

    @PostMapping
    @Operation(summary = "Crea una tarea nueva en la tabla tareas_jdbc")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarea creada"),
            @ApiResponse(responseCode = "400", description = "El titulo esta vacio")
    })
    ResponseEntity<TareaRequest> crear(@Valid @RequestBody TareaControllerCsv.NuevaTarea peticion) {
        TareaRequest tarea = serviceJdbc.crear(peticion.titulo());
        return ResponseEntity.created(URI.create("/api/jdbc/tareas/" + tarea.id())).body(tarea);
    }

    @PutMapping("/{id}") //genero el id
    @Operation(summary = "Actualiza el titulo de una tarea de la tabla tareas_jdbc")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea actualizada"),
            @ApiResponse(responseCode = "404", description = "No existe ninguna tarea con ese id")
    })//aqui dice lo de los dos parametros/elementos!
    ResponseEntity<TareaRequest> actualizar(@PathVariable long id, @Valid @RequestBody TareaControllerCsv.NuevaTarea peticion) {
        return ResponseEntity.ofNullable(serviceJdbc.actualizar(id, peticion.titulo()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borra una tarea de la tabla tareas_jdbc")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarea borrada"),
            @ApiResponse(responseCode = "404", description = "No existe tarea con ese id")
    })
    ResponseEntity<Void> borrar(@PathVariable long id) {
        if (serviceJdbc.borrar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
