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
//api/v1-lo que toque
//api/public
@RequestMapping("/api/json/tareas")
@Tag(name = "Tareas", description = "Operaciones CRUD de tareas en json")
public class TareaControllerJson {

    private final ITareaService serviceJson;

    public TareaControllerJson(@Qualifier("tareaServiceJson") ITareaService serviceJson) {
        this.serviceJson = serviceJson;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las tareas desde el fichero json")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Se obtiene el listado de tareas"))
    List<TareaRequest> listar() {
        return serviceJson.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener la tarea a traves de su id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Se obtiene la tarea indicada a traves de su id"),
            @ApiResponse(responseCode = "404", description = "No existe ninguna tarea con ese id")
    })
    ResponseEntity<TareaRequest> buscar(@PathVariable long id) {
        //return ResponseEntity.ofNullable(servicio.buscar(id));
        return ResponseEntity.ofNullable(serviceJson.buscar(id));
    }

    @PostMapping
    @Operation(summary = "Crea una tarea nueva en el fichero json")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarea creada"),
            @ApiResponse(responseCode = "400", description = "El titulo esta vacio")
    })
    ResponseEntity<TareaRequest> crear(@Valid @RequestBody TareaControllerCsv.NuevaTarea peticion) {
        //Tarea tarea = servicio.crear(peticion.titulo());
        //return ResponseEntity.created(URI.create("/api/tareas/" + tarea.id())).body(tarea);
        TareaRequest tarea = serviceJson.crear(peticion.titulo());
        return ResponseEntity.created(URI.create("/api/json/tareas/" + tarea.id())).body(tarea);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza el titulo de una tarea del fichero json")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarea actualizada"),
            @ApiResponse(responseCode = "404", description = "No existe ninguna tarea con ese id")
    })
    ResponseEntity<TareaRequest> actualizar(@PathVariable long id, @Valid @RequestBody TareaControllerCsv.NuevaTarea peticion) {
        return ResponseEntity.ofNullable(serviceJson.actualizar(id, peticion.titulo()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borra una tarea del fichero json")
    @ApiResponses({
            @ApiResponse (responseCode = "204", description = "Tarea borrada"),
            @ApiResponse (responseCode = "404", description = "No existe tarea con ese id")
    })
    ResponseEntity<Void> borrar (@PathVariable long id) {
        if (serviceJson.borrar(id)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
