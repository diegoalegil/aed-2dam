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
@RequestMapping("/api/xml/tareas")
@Tag(name = "Tareas", description = "Operaciones CRUD de tareas en xml")
public class TareaControllerXml {

    private final ITareaService serviceXml;

    public TareaControllerXml(@Qualifier("tareaServiceXml") ITareaService serviceXml) {
        this.serviceXml = serviceXml;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las tareas desde el fichero xml")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Se obtiene el listado de tareas"))
    List<TareaRequest> listar() {
        return serviceXml.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener la tarea a traves de su id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Se obtiene la tarea indicada a traves de su id"),
            @ApiResponse(responseCode = "404", description = "No existe ninguna tarea con ese id")
    })
    ResponseEntity<TareaRequest> buscar(@PathVariable long id) {
        return ResponseEntity.ofNullable(serviceXml.buscar(id));
    }

    @PostMapping
    @Operation(summary = "Crea una tarea nueva en el fichero xml")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarea creada"),
            @ApiResponse(responseCode = "400", description = "El titulo esta vacio")
    })
    ResponseEntity<TareaRequest> crear(@Valid @RequestBody TareaControllerCsv.NuevaTarea peticion) {
        TareaRequest tarea = serviceXml.crear(peticion.titulo());
        return ResponseEntity.created(URI.create("/api/xml/tareas/" + tarea.id())).body(tarea);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza el titulo de una tarea del fichero xml")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea actualizada"),
            @ApiResponse(responseCode = "404", description = "No existe ninguna tarea con ese id")
    })
    ResponseEntity<TareaRequest> actualizar(@PathVariable long id, @Valid @RequestBody TareaControllerCsv.NuevaTarea peticion) {
        return ResponseEntity.ofNullable(serviceXml.actualizar(id, peticion.titulo()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borra una tarea del fichero xml")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarea borrada"),
            @ApiResponse(responseCode = "404", description = "No existe tarea con ese id")
    })
    ResponseEntity<Void> borrar(@PathVariable long id) {
        if (serviceXml.borrar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
