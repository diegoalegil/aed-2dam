package aula.tareas.service;

import aula.tareas.modelo.Tarea;
import aula.tareas.repository.ITareaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tareaServiceXml")
public class TareaServiceXml implements ITareaService {

    private final ITareaRepository repository;

    public TareaServiceXml(@Qualifier("repositoryXml") ITareaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Tarea> listar() {
        return repository.findAll();
    }

    @Override
    public Tarea buscar(long id) {
        return repository.findById(id);
    }

    @Override
    public Tarea crear(String titulo) {

        if (titulo == null || titulo.isBlank()) {
            return null;
        }

        return repository.create(titulo);
    }

    @Override
    public Tarea actualizar(long id, String titulo) {

        if (titulo == null || titulo.isBlank()) {
            return null;
        }

        return repository.update(new Tarea(id, titulo.strip()));
    }

    @Override
    public boolean borrar(long id) {

        return repository.delete(id);
    }
}
